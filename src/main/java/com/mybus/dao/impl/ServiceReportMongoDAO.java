package com.mybus.dao.impl;

import com.mybus.SystemProperties;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import com.mybus.service.ServiceConstants;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


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

    public List<ServiceReport> findPendingReports(final Pageable pageable) throws ParseException {
        String startDate = systemProperties.getStringProperty("service.startDate", "2017-06-01");
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("status").exists(false), Criteria.where("journeyDate").gte(ServiceConstants.df.parse(startDate)));
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(query, ServiceReport.class).iterator());
        return reports;
    }
    public List<ServiceReport> findReportsToBeReviewed(final Pageable pageable) throws ParseException {
        String startDate = systemProperties.getStringProperty("service.startDate", "2017-06-01");
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("status").is(ServiceReport.ServiceReportStatus.REQUIRE_VERIFICATION),
                Criteria.where("journeyDate").gte(ServiceConstants.df.parse(startDate)));
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(query, ServiceReport.class).iterator());
        return reports;
    }

    public List<ServiceReport> findHaltedReports(Date date) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("status").is(ServiceReport.ServiceReportStatus.HALT),
                Criteria.where("journeyDate").gte(date));
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(query, ServiceReport.class).iterator());
        return reports;
    }
}
