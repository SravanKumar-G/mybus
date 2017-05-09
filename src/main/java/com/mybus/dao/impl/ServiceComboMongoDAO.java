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
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("serviceNumber").is(serviceNumber), Criteria.where("comboNumbers").is(serviceNumber));
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, ServiceCombo.class);
    }
}
