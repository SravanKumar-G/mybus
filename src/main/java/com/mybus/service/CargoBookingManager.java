package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.SupplierDAO;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/10/16.
 */
@Service
public class CargoBookingManager {
    private static final Logger logger = LoggerFactory.getLogger(CargoBookingManager.class);

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Autowired
    private CargoBookingMongoDAO cargoBookingMongoDAO;

    @Autowired
    private ShipmentSequenceManager shipmentSequenceManager;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private SMSManager smsManager;

    @Autowired
    private SupplierDAO supplierDAO;

    private Map<String, String> lrTypes;

    @Autowired
    private SessionManager sessionManager;

    public CargoBooking findOne(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "shipmentId is required");
        CargoBooking shipment = cargoBookingDAO.findByIdAndOperatorId(shipmentId, sessionManager.getOperatorId());
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        loadShipmentDetails(shipment);
        return shipment;
    }

    public String findByLRNumber(String LRNumber) {
        Preconditions.checkNotNull(LRNumber, "LRNumber is required");
        CargoBooking shipment = cargoBookingDAO.findByShipmentNumberAndOperatorId(LRNumber, sessionManager.getOperatorId());
        Preconditions.checkNotNull(shipment, "No CargoBooking found with LRNumebr",LRNumber);
        return shipment.getId();
    }

    /**
     * save cargo booking
     * @param cargoBooking
     * @return
     */
    public CargoBooking saveWithValidations(CargoBooking cargoBooking) {
        if(cargoBooking.getPaymentType() == null) {
            throw new BadRequestException("getPaymentType missing ");
        }
        shipmentSequenceManager.preProcess(cargoBooking);
        cargoBooking.setOperatorId(sessionManager.getOperatorId());
        List<String> errors = RequiredFieldValidator.validateModel(cargoBooking, CargoBooking.class);
        if( cargoBooking.getItems() != null) {
            for(CargoBookingItem cargoBookingItem: cargoBooking.getItems()){
                if(cargoBookingItem.getDescription() == null){
                    errors.add("Missing description for item ");
                }
            }
        }
        if(errors.isEmpty()) {
            sendSMSNotification(cargoBooking);
            try {
                cargoBooking.setCreatedAt(new DateTime());
                if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.PAID.toString())) {
                    updateUserCashBalance(cargoBooking);
                } else if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                    cargoBooking.setDue(true);
                } else if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                    cargoBooking.setDue(true);
                    updateOnAccountBalance(cargoBooking.getSupplierId(), cargoBooking.getTotalCharge(), false);
                }
                cargoBooking = cargoBookingDAO.save(cargoBooking);
            }catch (Exception e){
                cargoBookingDAO.delete(cargoBooking);
                throw e;
            }
        } else {
            throw new BadRequestException("Required data missing "+ String.join("<br> ", errors));
        }
        return cargoBooking;
    }

    /**
     * Pay cargobookings of types ToPay or OnAccount
     *
     * @param cargoBookingId
     */
    public boolean payCargoBooking(String cargoBookingId) {
        CargoBooking cargoBooking = cargoBookingDAO.findOne(cargoBookingId);
        if(cargoBooking == null || !cargoBooking.isDue()) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                updateUserCashBalance(cargoBooking);
                cargoBooking.setDue(false);
                cargoBooking.setPaidOn(new Date());
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                updateOnAccountBalance(cargoBooking.getSupplierId(), cargoBooking.getTotalCharge(), true);
                updateUserCashBalance(cargoBooking);
                cargoBooking.setDue(false);
                cargoBooking.setPaidOn(new Date());
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else {
                throw new BadRequestException("Invalid Type for CargoBooking, Only ToPay and OnAccount types can be paid");
            }
        }
    }

    /**
     *
     * @param supplierId
     * @param balance
     * @param isPaymentTransaction true when called from payBooking call, false when called from save booking call
     */

    private void updateOnAccountBalance(String supplierId, double balance, boolean isPaymentTransaction) {
        Supplier supplier = supplierDAO.findOne(supplierId);
        if(supplier == null) {
            throw new BadRequestException("Invalid supplier on OnAccount shipment");
        }
        if(isPaymentTransaction){
            supplier.setToBeCollected(supplier.getToBeCollected() - balance);
        } else {
            supplier.setToBeCollected(supplier.getToBeCollected() + balance);
        }
        supplierDAO.save(supplier);
    }


    /**
     * Update user cash balance for cargo booking
     * @param cargoBooking
     */
    private void updateUserCashBalance(CargoBooking cargoBooking) {
        Payment payment = paymentManager.createPayment(cargoBooking);
        if(payment == null){
            throw new BadRequestException("Failed to create payment for Cargo Booking");
        }
    }

    public CargoBooking updateShipment(String shipmentId, CargoBooking shipment) {
        Preconditions.checkNotNull(shipmentId, "ShipmentId can not be null");
        CargoBooking shipmentCopy = cargoBookingDAO.findOne(shipmentId);
        Preconditions.checkNotNull(shipmentCopy, "No CargoBooking found with id");
        try {
            shipmentCopy.merge(shipment, false);
        } catch (Exception e) {
            throw new BadRequestException("Error updating shipment");
        }
        return cargoBookingDAO.save(shipmentCopy);
    }
    public Iterable<CargoBooking> findShipments(JSONObject query, final Pageable pageable) throws ParseException {
        if(logger.isDebugEnabled()) {
            logger.debug("Looking up shipments with {0}", query);
        }
        List<CargoBooking> shipments = cargoBookingMongoDAO.findShipments(query, pageable);
        shipments.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return shipments;
    }

    public void delete(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "ShipmentId can not be null");
        CargoBooking shipment = cargoBookingDAO.findOne(shipmentId);
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        cargoBookingDAO.delete(shipment);
    }

    public Iterable<ShipmentSequence> getShipmentTypes() {
        return shipmentSequenceManager.getShipmentTypes();
    }

    /**
     * Module to populate details in to Cargo Shipment. The details would be like branchOfficeNames, createdBy etc..
     */
    private void loadShipmentDetails(CargoBooking cargoBooking){
        BranchOffice fromBranchOffice = branchOfficeManager.findOne(cargoBooking.getFromBranchId());
        BranchOffice toBranchOffice = branchOfficeManager.findOne(cargoBooking.getToBranchId());
        if(lrTypes == null){
            lrTypes = shipmentSequenceManager.getShipmentNamesMap();
        }
        cargoBooking.getAttributes().put("fromBranchOfficeAddress",String.format("%s, %s",fromBranchOffice.getAddress() , fromBranchOffice.getContact()));
        cargoBooking.getAttributes().put("toBranchOfficeAddress",String.format("%s, %s",toBranchOffice.getAddress() , toBranchOffice.getContact()));
        cargoBooking.getAttributes().put("fromBranchOfficeName",fromBranchOffice.getName());
        cargoBooking.getAttributes().put("toBranchOfficeName",toBranchOffice.getName());

        cargoBooking.getAttributes().put("LRType",lrTypes.get(cargoBooking.getPaymentType()));
        if(cargoBooking.getCreatedBy() != null){
            cargoBooking.getAttributes().put("bookedBy",userManager.getUser(cargoBooking.getCreatedBy()).getFullName());
        }
        if(cargoBooking.getForUser() != null) {
            cargoBooking.getAttributes().put("forUser",userManager.getUser(cargoBooking.getForUser()).getFullName());
        }
    }

    public long count(JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.countShipments(query);
    }

    private void sendSMSNotification(CargoBooking cargoBooking){

        BranchOffice fromBranchOffice = branchOfficeManager.findOne(cargoBooking.getFromBranchId());
        BranchOffice toBranchOffice = branchOfficeManager.findOne(cargoBooking.getToBranchId());

        String message = String.format("A parcel is booked with LR# %s From:%s(Ph:%s) " +
                "To:%s At %s, LRType:%s Amt:%s, Date:%s To:%s Contact %s for collecting. " +
                "Sri Krishna Cargo Services", cargoBooking.getShipmentNumber(), cargoBooking.getFromName(),
                String.valueOf(cargoBooking.getFromContact()), cargoBooking.getToName(),
                String.valueOf(cargoBooking.getToContact()),
                fromBranchOffice.getName(),cargoBooking.getPaymentType(),
                String.valueOf(cargoBooking.getTotalCharge()),
                cargoBooking.getCreatedAt(), toBranchOffice.getName(), toBranchOffice.getContact() +" "+ toBranchOffice.getAddress());
            try {
                smsManager.sendSMS(cargoBooking.getFromContact()+","+cargoBooking.getToContact(), message, "CargoBooking", cargoBooking.getId());
            } catch (UnirestException e) {
                e.printStackTrace();
                logger.error("Error sending SMS notification for cargo booking:" + cargoBooking.getId());
            }
    }

    /**
     * Cancel cargo booking
     * @param id
     * @return
     */
    public boolean cancelCargoBooking(String id) {
        CargoBooking cargoBooking = cargoBookingDAO.findOne(id);
        if(cargoBooking == null) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.PAID.toString())) {
                cargoBooking.setCanceled(true);
                cargoBooking.setCanceledOn(new Date());
                cargoBooking.setCanceldBy(sessionManager.getCurrentUser().getFullName());
                paymentManager.cancelCargoBooking(cargoBooking);
                cargoBooking.setCargoTransitStatus(CargoTransitStatus.CANCELLED);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                cargoBooking.setCanceled(true);
                cargoBooking.setCanceledOn(new Date());
                cargoBooking.setCanceldBy(sessionManager.getCurrentUser().getFullName());
                cargoBooking.setCargoTransitStatus(CargoTransitStatus.CANCELLED);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                //deduct the balance
                updateOnAccountBalance(cargoBooking.getSupplierId(), -cargoBooking.getTotalCharge(), false);
                cargoBooking.setCanceled(true);
                cargoBooking.setCanceledOn(new Date());
                cargoBooking.setCanceldBy(sessionManager.getCurrentUser().getFullName());
                cargoBooking.setCargoTransitStatus(CargoTransitStatus.CANCELLED);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else {
                throw new BadRequestException("Invalid Type for CargoBooking, Only ToPay and OnAccount types can be paid");
            }
        }
    }

    public JSONObject findContactInfo(String contactType, Long contact) {
        JSONObject jsonObject = new JSONObject();
        CargoBooking cargoBooking = null;
        if(contactType.equalsIgnoreCase("from")){
            cargoBooking = cargoBookingDAO.findOneByFromContactAndOperatorId(contact, sessionManager.getOperatorId());
            if(cargoBooking != null) {
                jsonObject.put("name", cargoBooking.getFromName());
                jsonObject.put("email", cargoBooking.getFromEmail());
            }
        }else if(contactType.equalsIgnoreCase("to")){
            cargoBooking = cargoBookingDAO.findOneByToContactAndOperatorId(contact, sessionManager.getOperatorId());
            if(cargoBooking != null) {
                jsonObject.put("name", cargoBooking.getToName());
                jsonObject.put("email", cargoBooking.getToEmail());
            }
        }
        return jsonObject;
    }
}
