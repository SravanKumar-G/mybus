package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.model.Person;
import com.mybus.model.ServiceReport;
import com.mybus.model.Shipment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceReportMongoDAO {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    public Iterable<ServiceReport> findReports(JSONObject query, final Pageable pageable) {
        String[] fields = {"serviceNumber", "serviceName", "busType", "status", "vehicleRegNumber", "netIncome",
                "source", "destination", "attrs"};
        Iterable<ServiceReport> reports = mongoQueryDAO.getDocuments(ServiceReport.class, ServiceReport.COLLECTION_NAME,
                fields, query, pageable);
        return reports;
    }


}
