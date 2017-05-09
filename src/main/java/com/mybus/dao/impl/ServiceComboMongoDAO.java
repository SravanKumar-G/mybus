package com.mybus.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mybus.model.ServiceCombo;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceComboMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public ServiceCombo findServiceCombo(String serviceNumber) {
        Query q = new Query();
        q.addCriteria(Criteria.where("serviceNumber").is(serviceNumber).orOperator(Criteria.where("comboNumbers").is(serviceNumber)));
        return mongoTemplate.findOne(q, ServiceCombo.class);
    }
}
