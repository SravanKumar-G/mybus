package com.mybus.dao.impl;

import com.mybus.model.Vehicle;
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

    public Iterable<Vehicle> findExpiring(Date date) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("permitExpiry").lte(date));
        match.add(where("insuranceExpiry").lte(date));
        match.add(where("pollutionExpiry").lte(date));
        match.add(where("authExpiry").lte(date));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return mongoTemplate.find(q, Vehicle.class);
    }
}
