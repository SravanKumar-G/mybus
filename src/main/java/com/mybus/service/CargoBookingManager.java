package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.dao.*;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.dto.BranchCargoBookingsSummary;
import com.mybus.dto.BranchwiseCargoBookingSummary;
import com.mybus.dto.UserCargoBookingsSummary;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.util.ServiceUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
    private BranchOfficeDAO branchOfficeDAO;

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

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    public CargoBooking findOne(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "shipmentId is required");
        CargoBooking shipment = cargoBookingDAO.findByIdAndOperatorId(shipmentId, sessionManager.getOperatorId());
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        loadShipmentDetails(shipment);
        return shipment;
    }

    public List<CargoBooking> findByLRNumber(String LRNumber) {
        Preconditions.checkNotNull(LRNumber, "LRNumber is required");
        List<CargoBooking> shipments = cargoBookingMongoDAO.findShipments(LRNumber);
        Preconditions.checkNotNull(shipments, "No CargoBooking found with LRNumebr",LRNumber);
        return shipments;
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
            sendBookingConfirmationSMS(cargoBooking);
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
    private CargoBooking payCargoBooking(String cargoBookingId, String deliveryNotes) {
        CargoBooking cargoBooking = cargoBookingDAO.findOne(cargoBookingId);
        if(cargoBooking == null || !cargoBooking.isDue()) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
            cargoBooking.setDeliveryNotes(deliveryNotes);
            cargoBooking.setDeliveredOn(new Date());
            cargoBooking.setDeliveredBy(sessionManager.getCurrentUser().getFullName());
            cargoBooking.setDue(false);
            cargoBooking.setPaidOn(new Date());
            cargoBooking.setCargoTransitStatus(CargoTransitStatus.DELIVERED);
            cargoBooking.setPaidBy(sessionManager.getCurrentUser().getFullName());
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                updateUserCashBalance(cargoBooking);
                return cargoBookingDAO.save(cargoBooking);
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                updateOnAccountBalance(cargoBooking.getSupplierId(), cargoBooking.getTotalCharge(), true);
                updateUserCashBalance(cargoBooking);
                return cargoBookingDAO.save(cargoBooking);
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
        BranchOffice fromBranchOffice = branchOfficeDAO.findOne(cargoBooking.getFromBranchId());
        BranchOffice toBranchOffice = branchOfficeDAO.findOne(cargoBooking.getToBranchId());
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

    /**
     * Send SMS for cargobooking
     * @param cargoBooking
     */
    private void sendBookingConfirmationSMS(CargoBooking cargoBooking){
        BranchOffice fromBranchOffice = branchOfficeDAO.findOne(cargoBooking.getFromBranchId());
        BranchOffice toBranchOffice = branchOfficeDAO.findOne(cargoBooking.getToBranchId());
        String cargoServiceName = "Cargo Services";
        if(sessionManager.getOperatorId() != null) {
            OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
            cargoServiceName = operatorAccount.getCargoServiceName();
        }
        String message = String.format("A parcel is booked with LR# %s From:%s(Ph:%s) " +
                "To:%s At %s, LRType:%s Amt:%s, Date:%s To:%s Contact %s for collecting. %s ", cargoBooking.getShipmentNumber(), cargoBooking.getFromName(),
                String.valueOf(cargoBooking.getFromContact()), cargoBooking.getToName(),
                String.valueOf(cargoBooking.getToContact()),
                fromBranchOffice.getName(),cargoBooking.getPaymentType(),
                String.valueOf(cargoBooking.getTotalCharge()),
                cargoBooking.getCreatedAt(), toBranchOffice.getName(),
                toBranchOffice.getContact() +" "+ toBranchOffice.getAddress(),
                cargoServiceName);
        try {
            smsManager.sendSMS(cargoBooking.getFromContact()+","+cargoBooking.getToContact(), message, "CargoBooking", cargoBooking.getId());
        } catch (UnirestException e) {
            e.printStackTrace();
            logger.error("Error sending SMS notification for cargo booking:" + cargoBooking.getId());
        }
    }

    private void sendBookingArrivalNotification(CargoBooking cargoBooking){
        BranchOffice toBranchOffice = branchOfficeDAO.findOne(cargoBooking.getToBranchId());
        String cargoServiceName = "Cargo Services";
        if(sessionManager.getOperatorId() != null) {
            OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
            cargoServiceName = operatorAccount.getCargoServiceName();
        }
        String message = String.format("A parcel with LR# %s From:%s(Ph:%s) " +
                        "has been arrived. Please collect it from %s. %s", cargoBooking.getShipmentNumber(), cargoBooking.getFromName(),
                String.valueOf(cargoBooking.getFromContact()), toBranchOffice.getAddress(), cargoServiceName);
        try {
            smsManager.sendSMS(cargoBooking.getToContact().toString(), message, "CargoBookingArrived", cargoBooking.getId());
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
            cargoBooking.getMessages().add(String.format("Cancelled by %s on %s", sessionManager.getCurrentUser().getFullName(), new DateTime()));
            cargoBooking.setCanceled(true);
            cargoBooking.setCanceledOn(new Date());
            cargoBooking.setCanceldBy(sessionManager.getCurrentUser().getFullName());
            cargoBooking.setCargoTransitStatus(CargoTransitStatus.CANCELLED);
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.PAID.toString())) {
                paymentManager.cancelCargoBooking(cargoBooking);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                //deduct the balance
                updateOnAccountBalance(cargoBooking.getSupplierId(), -cargoBooking.getTotalCharge(), false);
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

    /**
     * Module to re-send SMS for cargobooking
     * @param id
     * @return
     */
    public boolean sendSMSForCargoBooking(String id) {
        CargoBooking cargoBooking = cargoBookingDAO.findOne(id);
        if(cargoBooking != null) {
            sendBookingConfirmationSMS(cargoBooking);
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @param vehicleId
     * @param ids
     * @return
     */
    public boolean assignVehicle(String vehicleId, List<String> ids) {
        boolean assignmentSuccess = cargoBookingMongoDAO.assignVehicles(ids, vehicleId, sessionManager.getOperatorId());
        return assignmentSuccess;
    }

    /**
     * Deliver a cargobooking
     * @param id
     * @return
     */
    public CargoBooking deliverCargoBooking(String id, String deliveryNotes) {
        CargoBooking cargoBooking = cargoBookingDAO.findOne(id);
        if(cargoBooking == null){
            throw new IllegalArgumentException("Invalid cargo booking being delivered.");
        }
        if(cargoBooking.getCargoTransitStatus().toString().equalsIgnoreCase(CargoTransitStatus.DELIVERED.toString())){
            throw new BadRequestException("Cargo booking already delivered");
        }
        if(cargoBooking.isDue()){
            return payCargoBooking(id, deliveryNotes);
        } else {
            cargoBooking.setDeliveredBy(sessionManager.getCurrentUser().getFullName());
            cargoBooking.setDeliveryNotes(deliveryNotes);
            cargoBooking.setDeliveredOn(new Date());
            cargoBooking.setCargoTransitStatus(CargoTransitStatus.DELIVERED);
            return cargoBookingDAO.save(cargoBooking);
        }
    }

    /**
     * Get branch office summary for given date range
     * @param query
     * @return
     * @throws ParseException
     */
    public BranchwiseCargoBookingSummary getBranchSummary(JSONObject query) throws ParseException {
        BranchwiseCargoBookingSummary branchwiseCargoBookingSummary = new BranchwiseCargoBookingSummary();
        Iterable<CargoBooking> cargoBookings = findShipments(query, null);
        Map<String, BranchCargoBookingsSummary> branchCargoBookingsSummaryMap= new HashMap<>();
        Map<String, UserCargoBookingsSummary> userCargoBookingsSummaryMap= new HashMap<>();
        Map<String, String> branchNamesMap = branchOfficeManager.getNamesMap();
        Map<String, String> userNamesMap = userManager.getUserNames(true);
        for(CargoBooking cargoBooking:cargoBookings){
            BranchCargoBookingsSummary branchSummary = branchCargoBookingsSummaryMap.get(cargoBooking.getFromBranchId());
            if(branchSummary == null){
                branchSummary =  new BranchCargoBookingsSummary();
                branchSummary.setBranchOfficeId(cargoBooking.getFromBranchId());
                branchSummary.setBranchOfficeName(branchNamesMap.get(cargoBooking.getFromBranchId()));
                branchCargoBookingsSummaryMap.put(cargoBooking.getFromBranchId(), branchSummary);
            }
            UserCargoBookingsSummary userSummary = userCargoBookingsSummaryMap.get(cargoBooking.getCreatedBy());
            if(userSummary == null){
                userSummary =  new UserCargoBookingsSummary();
                userSummary.setUserId(cargoBooking.getCreatedBy());
                userSummary.setUserName(userNamesMap.get(cargoBooking.getCreatedBy()));
                userCargoBookingsSummaryMap.put(cargoBooking.getCreatedBy(), userSummary);
            }
            if(cargoBooking.getPaymentType().equals(PaymentStatus.PAID.getKey())){
                branchSummary.setPaidBookingsTotal(branchSummary.getPaidBookingsTotal()+ cargoBooking.getTotalCharge());
                branchSummary.setPaidBookingsCount(branchSummary.getPaidBookingsCount()+1);
                userSummary.setPaidBookingsTotal(userSummary.getPaidBookingsTotal()+ cargoBooking.getTotalCharge());
                userSummary.setPaidBookingsCount(userSummary.getPaidBookingsCount()+1);
            } else if(cargoBooking.getPaymentType().equals(PaymentStatus.TOPAY.getKey())){
                branchSummary.setTopayBookingsTotal(branchSummary.getTopayBookingsTotal()+ cargoBooking.getTotalCharge());
                branchSummary.setTopayBookingsCount(branchSummary.getTopayBookingsCount()+1);
                userSummary.setTopayBookingsTotal(userSummary.getTopayBookingsTotal()+cargoBooking.getTotalCharge());
                userSummary.setTopayBookingsCount(userSummary.getTopayBookingsCount() + 1);
            } else if(cargoBooking.getPaymentType().equals(PaymentStatus.ONACCOUNT.getKey())){
                branchSummary.setOnAccountBookingsTotal(branchSummary.getOnAccountBookingsTotal()+ cargoBooking.getTotalCharge());
                branchSummary.setOnAccountBookingsCount(branchSummary.getOnAccountBookingsCount()+1);
                userSummary.setOnAccountBookingsTotal(userSummary.getOnAccountBookingsTotal() + cargoBooking.getTotalCharge());
                userSummary.setOnAccountBookingsCount(userSummary.getOnAccountBookingsCount()+1);
            }
        }
        branchwiseCargoBookingSummary.getBranchCargoBookings().addAll(branchCargoBookingsSummaryMap.values());
        branchwiseCargoBookingSummary.getUserCargoBookingsSummaries().addAll(userCargoBookingsSummaryMap.values());
        return branchwiseCargoBookingSummary;
    }

    /**
     *
     * @param branchOfficeId
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public BranchwiseCargoBookingSummary getBranchwiseCargoBookingSummary(String branchOfficeId, Date start, Date end) throws ParseException {
        start = ServiceUtils.parseDate(start, false);
        end = ServiceUtils.parseDate(end, true);
        BranchwiseCargoBookingSummary branchwiseCargoBookingSummary = null;
        if(branchOfficeId != null) {
            branchwiseCargoBookingSummary = cargoBookingMongoDAO.getBranchwiseBookingSummary(branchOfficeId, start, end);
        } else {
            branchwiseCargoBookingSummary = cargoBookingMongoDAO.getAllBranchsBookingSummary(start, end);
        }
        //fill in the branch office names
        Map<String,String> branchOfficeNames = branchOfficeManager.getNamesMap();
        for(BranchCargoBookingsSummary summary: branchwiseCargoBookingSummary.getBranchCargoBookings()){
            summary.setBranchOfficeName(branchOfficeNames.get(summary.getBranchOfficeId()));
        }
        return branchwiseCargoBookingSummary;
    }

    /**
     * Get summary of all bookings
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public BranchwiseCargoBookingSummary getAllBranchCargoBookingSummary(Date start, Date end) throws ParseException {
        start = ServiceUtils.parseDate(start, false);
        end = ServiceUtils.parseDate(end, true);
        return cargoBookingMongoDAO.getAllBranchsBookingSummary(start, end);
    }

    /**
     * Find bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForUnloading(JSONObject query) throws ParseException {
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findShipmentsForUnloading(query);
        cargoBookings.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return cargoBookings;
    }

    /**
     * Unload bookings and send arrival SMS notifications
     * @param bookingIds
     * @return
     */
    public boolean unloadBookings(List<String> bookingIds){
        if(cargoBookingMongoDAO.unloadBookings(bookingIds)){
            bookingIds.stream().forEach(id -> {
                sendBookingArrivalNotification(cargoBookingDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId()));
            });
            return true;
        };
        return false;
    }

    public List<CargoBooking> findUndeliveredShipments(JSONObject query) throws ParseException {
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findUndeliveredShipments(query);
        cargoBookings.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return cargoBookings;
    }
}
