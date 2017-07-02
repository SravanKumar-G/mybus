package com.mybus.dao.impl;

import com.mybus.SystemProperties;
import com.mybus.model.ServiceCombo;
import com.mybus.model.ServiceReport;
import com.mybus.service.ServiceConstants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceReportMongoDAO {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SystemProperties systemProperties;

    public Iterable<ServiceReport> findReports(JSONObject query, final Pageable pageable) {
        String[] fields = {"serviceNumber", "serviceName", "busType", "status", "vehicleRegNumber", "netIncome", "netCashIncome",
                "source", "destination", "attrs"};
        Iterable<ServiceReport> reports = mongoQueryDAO.getDocuments(ServiceReport.class, ServiceReport.COLLECTION_NAME,
                fields, query, pageable);
        return reports;
    }

    public Iterable<ServiceReport> findPendingReports(final Pageable pageable) throws ParseException {
        String startDate = systemProperties.getStringProperty("service.startDate", "2017-06-01");
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("status").exists(false), Criteria.where("journeyDate").gte(ServiceConstants.df.parse(startDate)));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, ServiceReport.class);
    }

}
