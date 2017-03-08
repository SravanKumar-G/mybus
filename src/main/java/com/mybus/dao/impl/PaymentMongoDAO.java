package com.mybus.dao.impl;

import com.mybus.dao.PaymentDAO;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import com.mybus.service.ServiceConstants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class PaymentMongoDAO {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Payment save(Payment payment){
        return paymentDAO.save(payment);
    }

    public Payment update(Payment payment){
        return paymentDAO.save(payment);
    }

    public long count(JSONObject query) {
        return  mongoTemplate.count(prepareExpenseQuery(query), Payment.class);
    }

    public Iterable<Payment> find(JSONObject query) {
        return  mongoTemplate.find(prepareExpenseQuery(query), Payment.class);
    }

    public Query prepareExpenseQuery(JSONObject query) {
        Query q = new Query();
        if(query == null) {
            return q;
        }
        if(query.containsKey("description")) {
            q.addCriteria(Criteria.where("description").regex(query.get("description").toString()));
        }
        if(query.containsKey("startDate")){
            String startDate = query.get("startDate").toString();
            try {
                Date start = ServiceConstants.df.parse(startDate);

                q.addCriteria(Criteria.where("startDate").gte(start));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(query.containsKey("endDate")){
            String startDate = query.get("endDate").toString();
            try {
                Date start = ServiceConstants.df.parse(startDate);
                q.addCriteria(Criteria.where("endDate").lte(start));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return q;
    }
}
