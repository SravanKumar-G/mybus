package com.mybus;

import com.google.common.base.Preconditions;
import com.mybus.controller.UserController;
import com.mybus.dao.CityDAO;
import com.mybus.dao.impl.CityMongoDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 12/9/15.
 */
@Service
public class CityManager {
    private static final Logger logger = LoggerFactory.getLogger(CityManager.class);

    @Autowired
    private CityMongoDAO cityMongoDAO;

    @Autowired
    private CityDAO cityDAO;

    public City addBoardingPointToCity(String cityId, BoardingPoint bp) {
        return cityMongoDAO.addBoardingPoint(cityId, bp);
    }

    public boolean delete(String id) {
        Preconditions.checkNotNull(id, "The city id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting city with id" + id);
        }
        try {
            cityDAO.delete(id);
        } catch (Exception e) {
            logger.error("Error deleting city with id " + id, e);
            return false;
        }
        return true;
    }

    public City save(City city) {
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city state can not be null");
        return cityDAO.save(city);
    }
}
