package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.dao.CityDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        List<BoardingPoint> bps = city.getBoardingPoints();
        validateBoardingPoint(boardingPoint, bps);
        bps.add(boardingPoint);
        city.setBoardingPoints(bps);
        return cityDAO.save(city);
    }

    public void validateBoardingPoint(BoardingPoint boardingPoint, List<BoardingPoint> bps) {
        List<BoardingPoint> matchingBps = bps.stream().filter(bp -> bp.getName()
                .equals(boardingPoint.getName()) && !bp.getId().equals(boardingPoint.getId()))
                .collect(Collectors.toList());
        if(matchingBps.size() > 0) {
            throw new RuntimeException("A boardingpoint already exists with name:"+ boardingPoint.getName());
        }
    }

}
