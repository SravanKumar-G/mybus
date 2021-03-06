package com.mybus.dao.impl;

import com.mybus.dao.PaymentDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Payment;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceConstants;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class PaymentMongoDAO {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceUtils serviceUtils;

    public Payment save(Payment payment){
        if(payment.getDescription() == null || payment.getDescription().trim().length() == 0) {
            throw new BadRequestException("Description is required");
        }
        return paymentDAO.save(payment);
    }

    public void save(List<Payment> payments){
        for(Payment payment: payments) {
           save(payment);
        }
    }

    public Payment update(Payment payment){
        return paymentDAO.save(payment);
    }

    public long count(JSONObject query) {
        return  mongoTemplate.count(preparePaymentQuery(query, null), Payment.class);
    }

    public Iterable<Payment> find(JSONObject query, Pageable pageable) {
        return  mongoTemplate.find(preparePaymentQuery(query, pageable), Payment.class);
    }

    public Query preparePaymentQuery(JSONObject query, Pageable pageable)  {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        //filter the form expenses from the report
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("operatorId").is(sessionManager.getOperatorId()));
        try{
            //filter the payments by office if the user is not admin
            if(!sessionManager.getCurrentUser().isAdmin()) {
                match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
            }
            if(query != null) {
                Object start = query.get("startDate");
                Object end = query.get("endDate");
                Date s = null,e = null;
                if(start != null) {
                    s = ServiceConstants.parseDate(start.toString());
                }
                if(end != null) {
                    e = ServiceConstants.parseDate(end.toString());
                }
                MongoQueryDAO.createTimeFrameQuery("date", s, e, match);
                for(Object key:query.keySet()) {
                    String keyStr = key.toString();
                    if(keyStr.equalsIgnoreCase("startDate") || keyStr.equalsIgnoreCase("endDate")) {
                        continue;
                    }
                    if (keyStr.equals("description")) {
                        match.add(Criteria.where("description").regex(query.get("description").toString()));
                    } else  {
                        if(query.get(keyStr) == null || query.get(keyStr).toString().equals("null")) {
                            match.add(where(keyStr).exists(false));
                        }else {
                            match.add(where(keyStr).is(query.get(keyStr).toString()));
                        }
                    }
                }
            }
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
            if(pageable != null) {
                q.with(pageable);
            }
        }catch (Exception e) {
            throw new BadRequestException("Exception preparing payment query", e);
        }
        return q;
    }

    public long getPaymentsCount(boolean pending) {
        Query q = getPaymentsQuery(pending, null);
        return mongoTemplate.count(q, Payment.class);
    }

    public long getVehicleExpensesCount() {
        Query q = getVehicleExpensesQuery();
        return mongoTemplate.count(q, Payment.class);
    }
    public Page<Payment> getVehicleExpenses(Pageable pageable) {
        Query q = getVehicleExpensesQuery();
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }

    public Page<Payment> findPendingPayments(Pageable pageable) {
        Query q = getPaymentsQuery(true, pageable);
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments);
    }
    public Page<Payment> findNonPendingPayments(Pageable pageable) {
        Query q = getPaymentsQuery(false, pageable);

        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }

    /**
     * Find payments for given date
     * @param date
     * @param pageable
     * @return
     */
    public Page<Payment> findPaymentsByDate(String date, Pageable pageable) {
        Query q = getPaymentsByDateQuery(date);
        if(pageable != null) {
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }
    
    private Query getPaymentsQuery(boolean pending, Pageable pageable) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(sessionManager != null && sessionManager.getCurrentUser() != null && !sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("serviceReportId").exists(false));
        match.add(where("vehicleId").exists(false));
        if(pending) {
            match.add(where("status").exists(false));
        }else {
            match.add(where("status").exists(true));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        if(pageable == null) {
            q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        } else {
            q.with(pageable);
        }
        return q;
    }

    private Query getVehicleExpensesQuery() {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(!sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        match.add(Criteria.where("formId").exists(false));
        match.add(where("vehicleId").exists(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }
    
    private Query getPaymentsByDateQuery(String date) {
        Query q = new Query();
        Date startDate = null, endDate = null;
        try{
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy/MM/dd hh:mm");
        	String startTime = date + " 00:00"; 
        	String endTime = date + " 24:00";
        	startDate = simpleDateFormat.parse(startTime);  
        	endDate = simpleDateFormat.parse(endTime);
        }catch(Exception e){
        	throw new BadRequestException("Exception preparing payment query", e);
        }
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        //show the submitted form payments as well
        //match.add(Criteria.where("date").gte(startDate).lt(endDate));
        match.add(Criteria.where("submittedBy").is(sessionManager.getCurrentUser().getId()));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(Criteria.where("createdAt").gte(startDate).lt(endDate));
        //skip form expenses
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("status").ne(Payment.STATUS_PENDING));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }

    public List<Payment> search(JSONObject query, Pageable pageable) throws ParseException {
        Query q = serviceUtils.createSearchQuery(query, pageable);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return payments;
    }
}
