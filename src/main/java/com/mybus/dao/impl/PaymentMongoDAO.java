package com.mybus.dao.impl;

import com.mybus.dao.PaymentDAO;
import com.mybus.model.Payment;
import com.mybus.service.ServiceConstants;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;

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

    public Payment save(Payment payment){
        return paymentDAO.save(payment);
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

    public Query preparePaymentQuery(JSONObject query, Pageable pageable) {
        Query q = new Query();
        //filter the form expenses from the report
        q.addCriteria(Criteria.where("formId").exists(false));
        //filter the payments by office if the user is not admin
        if(!sessionManager.getCurrentUser().isAdmin()) {
            q.addCriteria(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        if(query != null) {
            for(Object key:query.keySet()) {
                String keyStr = key.toString();
                if (keyStr.equals("description")) {
                    q.addCriteria(Criteria.where("description").regex(query.get("description").toString()));
                }
                if (keyStr.equals("startDate")) {
                    String startDate = query.get("startDate").toString();
                    try {
                        Date start = ServiceConstants.df.parse(startDate);

                        q.addCriteria(Criteria.where("startDate").gte(start));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (keyStr.equals("endDate")) {
                    String startDate = query.get("endDate").toString();
                    try {
                        Date start = ServiceConstants.df.parse(startDate);
                        q.addCriteria(Criteria.where("endDate").lte(start));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else  {
                    if(query.get(keyStr) != null) {
                        String queryVal = query.get(keyStr).toString();
                        if(queryVal.equalsIgnoreCase("null")){
                            q.addCriteria(where(keyStr).exists(false));
                        }else {
                            q.addCriteria(where(keyStr).is(queryVal));
                        }
                    }
                }
            }
        }
        if(pageable != null) {
            q.with(pageable);
        }
        return q;
    }
}
