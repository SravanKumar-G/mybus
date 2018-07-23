package com.mybus.dao.impl;

import com.amazonaws.services.kinesis.model.InvalidArgumentException;
import com.mybus.SystemProperties;
import com.mybus.model.ServiceReport;
import com.mybus.service.ServiceConstants;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
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

import static org.springframework.data.mongodb.core.query.Criteria.where;


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

    @Autowired
    private SessionManager sessionManager;

    public Iterable<ServiceReport> findReports(String date, final Pageable pageable) throws ParseException {
        String[] fields = {"serviceNumber", "serviceName", "busType", "status", "vehicleRegNumber", "netIncome", "netCashIncome",
                "source", "destination", "attrs"};
        Query q = new Query();
        q.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        q.addCriteria(where(ServiceReport.JOURNEY_DATE).gte(ServiceUtils.parseDate(date, false)).lte(ServiceUtils.parseDate(date, true)));
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        return reports;
    }

    public Iterable<ServiceReport> findPendingReports(final Pageable pageable, String operatorId) throws ParseException {
        return getServiceReports(null, null, operatorId);
    }
    public Iterable<ServiceReport> findReportsToBeReviewed(final Pageable pageable, String operatorId) throws ParseException {
        return getServiceReports(ServiceReport.ServiceReportStatus.REQUIRE_VERIFICATION.toString(), null, operatorId);
    }

    public Iterable<ServiceReport> findHaltedReports(Date date, String operatorId) throws ParseException {
        return getServiceReports(ServiceReport.ServiceReportStatus.HALT.toString(), date, operatorId);
    }

    private Iterable<ServiceReport> getServiceReports(String status, Date date, String operatorId) throws ParseException {
        Date startDate = ServiceConstants.parseDate(systemProperties.getStringProperty("service.startDate", "2017-06-01"));
        if(date != null) {
            startDate = date;
        }
        Criteria criteria = new Criteria();
        if(operatorId == null){
            operatorId = sessionManager.getOperatorId();
        }
        if(operatorId == null) {
            throw new InvalidArgumentException("OperatorId not found");
        }
        if(status == null){
            criteria.andOperator(Criteria.where("status").exists(false),
                    Criteria.where("journeyDate").gte(startDate),Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        } else {
            criteria.andOperator(Criteria.where("status").is(status),
                    Criteria.where("journeyDate").gte(startDate),Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"createdAt"));
        query.fields().exclude("bookings");
        query.fields().exclude("expenses");
        query.fields().exclude("serviceExpense");
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(query, ServiceReport.class).iterator());
        return reports;
    }
}
