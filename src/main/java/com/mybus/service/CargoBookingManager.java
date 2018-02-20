package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.CargoBooking;
import com.mybus.model.cargo.ShipmentSequence;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

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


    public CargoBooking findOne(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "shipmentId is required");
        CargoBooking shipment = cargoBookingDAO.findOne(shipmentId);
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        return shipment;
    }
    public CargoBooking saveWithValidations(CargoBooking shipment) {
        if(shipment.getShipmentType() == null) {
            throw new BadRequestException("ShipmentType missing ");
        }
        String shipmentNumber = shipmentSequenceManager.createLRNumber(shipment.getShipmentType());
        shipment.setShipmentNumber(shipmentNumber);
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
}
