package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
public class RouteMongoDAO {
    
    @Autowired
    private RouteDAO routeDAO;
    
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;


    public long count() {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(query, Route.class);
    }
}
