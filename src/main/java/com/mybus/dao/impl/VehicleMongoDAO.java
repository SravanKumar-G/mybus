package com.mybus.dao.impl;

import com.mybus.model.Vehicle;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class VehicleMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public Iterable<Vehicle> findExpiring(Date date) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("permitExpiry").lte(date));
        match.add(where("insuranceExpiry").lte(date));
        match.add(where("pollutionExpiry").lte(date));
        match.add(where("authExpiry").lte(date));
        //match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.orOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return mongoTemplate.find(q, Vehicle.class);
    }
}
