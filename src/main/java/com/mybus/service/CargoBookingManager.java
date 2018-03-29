package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BranchOffice;
import com.mybus.model.CargoBooking;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public CargoBooking saveWithValidations(CargoBooking shipment) {
        if(shipment.getShipmentType() == null) {
            throw new BadRequestException("ShipmentType missing ");
        }
        String shipmentNumber = shipmentSequenceManager.createLRNumber(shipment.getShipmentType());
        shipment.setShipmentNumber(shipmentNumber);
        shipment.setOperatorId(sessionManager.getOperatorId());
        shipment.setActive(true);
        List<String> errors = RequiredFieldValidator.validateModel(shipment, CargoBooking.class);
        if(errors.isEmpty()) {
            return cargoBookingDAO.save(shipment);
        } else {
            throw new BadRequestException("Required data missing ");
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
        query = ServiceUtils.addOperatorId(query, sessionManager);
        return cargoBookingMongoDAO.findShipments(query, pageable);
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
}
