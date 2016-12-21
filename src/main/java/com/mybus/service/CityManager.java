package com.mybus.service;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mybus.dao.CityDAO;
import com.mybus.dao.impl.CityMongoDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    public City findOne(String cityId){
        return cityDAO.findOne(cityId);
    }

    public City findCityByName(String name) {
        return cityDAO.findOneByName(name);
    }
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

    public City saveCity(City city){
        Preconditions.checkNotNull(city, "The city can not be null");
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city State can not be null");
        if (cityDAO.findOneByNameAndState(city.getName(), city.getState()) != null) {
            throw new BadRequestException("A city already exists with same name and state");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Saving city:[{}]" + city);
        }

        return cityDAO.save(city);
    }

    public boolean updateCity(City city) {
        Preconditions.checkNotNull(city, "The city can not be null");
        Preconditions.checkNotNull(city.getId(), "The city id can not be null");
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city State can not be null");
        City matchingCity = cityDAO.findOneByNameAndState(city.getName(), city.getState());
        if(matchingCity != null && !city.getId().equals(matchingCity.getId())) {
            throw new BadRequestException("A city already exists with same name and state");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Updating city:[{}]" + city);
        }
        return cityMongoDAO.updateCity(city);
    }

    public City addBoardingPointToCity(String cityId, BoardingPoint bp) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(bp.getName(), "Boarding point name can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Adding boarding point to city with id" + cityId);
        }
        if (cityDAO.findOne(cityId) == null) {
            throw new BadRequestException("Unknown city id");
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
        cityMongoDAO.validateBoardingPoint(bp, city.getBoardingPoints());
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
                    .filter(b -> !b.getId().equals(id)).collect(Collectors.toList()));
            return cityDAO.save(city);
        } else {
            throw new BadRequestException("Boarding point not found");
        }
    }

    public BoardingPoint getBoardingPoint(String cityId, String id) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(id, "The boarding point id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("searching for boarding point:[{}] from city:[{}]", id, cityId);
        }
        City city = cityDAO.findOne(cityId);
        Optional<BoardingPoint> bp = city.getBoardingPoints().stream().filter(b -> b.getId().equals(id)).findFirst();
        if (bp.isPresent()) {
            return bp.get();
        }
        return null;
    }

    /**
     * Module to build a map of <cityId, cityName>,
     * @param allCities -- when true all the names will be returned, when false only active city names are returned
     * @return
     */
    public Map<String, String> getCityNames(boolean allCities) {
        String fields[] = {City.KEY_NAME};
        JSONObject query = new JSONObject();
        if(!allCities) {
            query.put("active", true);
        }
        List<City> cities = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(City.class, City.COLLECTION_NAME, fields, query, null).iterator());
        Map<String, String> map = cities.stream().collect(
                Collectors.toMap(City::getId, city -> city.getName()));
        return map;
    }

    public Iterable<City> findAll(){
        return cityDAO.findAll();
    }
    public void deleteAll() {
        cityDAO.deleteAll();
    }
    
    public Iterable<BoardingPoint> getBoardingPoints(String cityId) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("getting boarding points for city:[{}] ", cityId);
        }
        City city = cityDAO.findOne(cityId);
        return city.getBoardingPoints();
    }
}
