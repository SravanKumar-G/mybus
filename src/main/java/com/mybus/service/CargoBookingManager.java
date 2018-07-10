package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BranchOffice;
import com.mybus.model.CargoBooking;
import com.mybus.model.CargoBookingItem;
import com.mybus.model.Payment;
import com.mybus.model.cargo.ShipmentSequence;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    public CargoBooking saveWithValidations(CargoBooking shipment) {
        if(shipment.getShipmentType() == null) {
            throw new BadRequestException("ShipmentType missing ");
        }
        shipmentSequenceManager.preProcess(shipment);
        shipment.setOperatorId(sessionManager.getOperatorId());
        List<String> errors = RequiredFieldValidator.validateModel(shipment, CargoBooking.class);
        if( shipment.getItems() != null) {
            for(CargoBookingItem cargoBookingItem: shipment.getItems()){
                if(cargoBookingItem.getDescription() == null){
                    errors.add("Missing description for item ");
                }
            }
        }
        if(errors.isEmpty()) {
            shipment = cargoBookingDAO.save(shipment);
            if(shipment.getId() != null) {
                updateUserCashBalance(shipment);
            } else {
                throw new BadRequestException("Failed creating shipment");
            }
        } else {
            throw new BadRequestException("Required data missing "+ String.join("<br> ", errors));
        }
        return shipment;
    }

    /**
     * Update user cash balance for cargo booking
     * @param cargoBooking
     */
    private void updateUserCashBalance(CargoBooking cargoBooking) {
        if(cargoBooking.getShipmentNumber().startsWith(ShipmentSequence.PAID_TYPE)){
            Payment payment = paymentManager.createPayment(cargoBooking);
            if(payment == null){
                throw new BadRequestException("Failed to create payment for Cargo Booking");
            }
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

        cargoBooking.getAttributes().put("LRType",lrTypes.get(cargoBooking.getShipmentType()));
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
}
