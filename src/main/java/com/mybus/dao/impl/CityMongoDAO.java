package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.dao.CityDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.Person;
import com.mybus.service.SessionManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class CityMongoDAO {
    
    @Autowired
    private CityDAO cityDAO;
    
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public City save(City city){
        return cityDAO.save(city);
    }
    public City update(City city){
        return cityDAO.save(city);
    }

    public boolean updateCity(City city) {
        Update updateOp = new Update();
        updateOp.set("name", city.getName());
        updateOp.set("active", city.isActive());
        updateOp.set("state", city.getState());
        final Query query = new Query();
        query.addCriteria(where("_id").is(city.getId()));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, City.class);
        return writeResult.getN() == 1;
    }
    public City addBoardingPoint(String cityId, BoardingPoint boardingPoint) {
        City city = cityDAO.findOne(cityId);
        city.getBoardingPoints().add(boardingPoint);
        return cityDAO.save(city);
    }

}
