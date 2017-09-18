package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.VehicleDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class VehicleManager {
    private static final Logger logger = LoggerFactory.getLogger(VehicleManager.class);

    @Autowired
    private VehicleDAO vehicleDAO;

    public Vehicle saveVehicle(Vehicle vehicle){
        vehicle.validate();
        Vehicle duplicateVehicle = vehicleDAO.findOneByRegNo(vehicle.getRegNo());
        if (duplicateVehicle != null && !duplicateVehicle.getId().equals(vehicle.getId())) {
            throw new RuntimeException("A Vehicle already exists with the same Registration number");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Saving Vehicle: [{}]", vehicle);
        }
        return vehicleDAO.save(vehicle);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        Preconditions.checkNotNull(vehicle.getId(), "Unknown vehicle id");
        Vehicle loadedVehicle = vehicleDAO.findOne(vehicle.getId());
        try {
            loadedVehicle.merge(vehicle);
        }catch (Exception e) {
            logger.error("Error merging vehicle", e);
            throw new BadRequestException("Error merging vehicle info");
        }
        return saveVehicle(loadedVehicle);
    }

    public boolean deleteVehicle(String vehicleId){
        Preconditions.checkNotNull(vehicleId, "The vehicleId can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting vehicle:[{}]" + vehicleId);
        }
        if (vehicleDAO.findOne(vehicleId) != null) {
            vehicleDAO.delete(vehicleId);
        } else {
            throw new RuntimeException("Unknown user id");
        }
        return true;
    }

    /**
     * Find a vehicle by either registration number or chasis number or engine number
     * @param key
     * @return
     */
    public Vehicle findVehicle(String key) {
        return null;
    }
}
