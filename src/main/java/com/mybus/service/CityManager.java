package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CityDAO;
import com.mybus.dao.impl.CityMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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

    public boolean deleteCity(String id) {
        Preconditions.checkNotNull(id, "The city id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting city:[{}]" + id);
        }
        if (cityDAO.findOne(id) != null) {
            cityDAO.delete(id);
        } else {
            throw new RuntimeException("Unknown city id");
        }
        return true;
    }

    public City saveCity(City city) {
        Preconditions.checkNotNull(city, "The city can not be null");
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city State can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Saving city:[{}]" + city);
        }
        return cityDAO.save(city);
    }
    public City addBoardingPointToCity(String cityId, BoardingPoint bp) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(bp.getName(), "Boarding point name can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Adding boarding point to city with id" + cityId);
        }
        if (cityDAO.findOne(cityId) == null) {
            throw new RuntimeException("Unknown city id");
        }
        return cityMongoDAO.addBoardingPoint(cityId, bp);
    }
    public City updateBoardingPoint(String cityId, final BoardingPoint bp) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(bp.getName(), "Boarding point name can not be null");
        Preconditions.checkNotNull(bp.getId(), "Boarding point ID can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Updating boarding point:[{}] to city:[{}]", bp.getId(), cityId);
        }
        City city = cityDAO.findOne(cityId);
        city.getBoardingPoints().stream().filter(b -> b.getId().equals(bp.getId())).forEach(b -> {
            try {
                b.merge(bp);
            } catch (Exception e ) {
                logger.error("error merging boarding point", e);
                throw new RuntimeException("error merging boarding point", e);
            }
        });
        return cityDAO.save(city);
    }


    public City deleteBoardingPoint(String cityId, String id) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(id, "The boarding point id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting boarding point:[{}] from city:[{}]", id, cityId);
        }
        City city = cityDAO.findOne(cityId);
        Preconditions.checkNotNull(city, "No city found with id:[{}]", cityId);
        if(city.getBoardingPoints().stream().filter(b -> b.getId().equals(id)).count() == 1) {
            city.setBoardingPoints(city.getBoardingPoints().stream()
                    .filter(b -> !b.getId().equals(id)).collect(Collectors.toSet()));
            return cityDAO.save(city);
        } else {
            throw new BadRequestException("Boarding point not found");
        }
    }
}
