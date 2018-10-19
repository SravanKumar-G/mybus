package com.mybus.dao.impl;

import com.mybus.model.FullTrip;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class FullTripMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public List<FullTrip> findPending(Date start, Date end) {
        final Query query = new Query();
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(Criteria.where("due").is(true));
        if(start != null) {
            query.addCriteria(Criteria.where("tripDate").gte(start));
        }
        if(end != null) {
            query.addCriteria(Criteria.where("tripDate").lte(end));
        }
        List<FullTrip> fullTrips = mongoTemplate.find(query, FullTrip.class);
        return fullTrips;
    }

    public List<FullTrip> findPaid(Date start, Date end) {
        final Query query = new Query();
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(Criteria.where("due").is(false));
        if(start != null) {
            query.addCriteria(Criteria.where("tripDate").gte(start));
        }
        if(end != null) {
            query.addCriteria(Criteria.where("tripDate").lte(end));
        }
        List<FullTrip> fullTrips = mongoTemplate.find(query, FullTrip.class);
        return fullTrips;
    }

}
