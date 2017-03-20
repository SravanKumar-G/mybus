package com.mybus.dao.impl;

import com.mybus.model.CashTransfer;
import com.mybus.service.ServiceConstants;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by srinikandula on 3/19/17.
 */
@Service
public class CashTransferMongoDAO {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public long count(JSONObject query) {
        return  mongoTemplate.count(preparePaymentQuery(query, null), CashTransfer.class);
    }

    public Iterable<CashTransfer> find(JSONObject query, Pageable pageable) {
        return  mongoTemplate.find(preparePaymentQuery(query, pageable), CashTransfer.class);
    }

    public Query preparePaymentQuery(JSONObject query, Pageable pageable) {
        Query q = new Query();
        //filter the payments by office if the user is not admin
        if(!sessionManager.getCurrentUser().isAdmin()) {
            q.addCriteria(Criteria.where(CashTransfer.FROM_OFFICE_ID).is(sessionManager.getCurrentUser().getBranchOfficeId())
                    .orOperator(Criteria.where(CashTransfer.TO_OFFICE_ID).is(sessionManager.getCurrentUser().getBranchOfficeId())));
        }
        if(query != null) {
            if (query.containsKey("description")) {
                q.addCriteria(Criteria.where("description").regex(query.get("description").toString()));
            }
            if (query.containsKey("startDate")) {
                String startDate = query.get("date").toString();
                try {
                    Date start = ServiceConstants.df.parse(startDate);
                    q.addCriteria(Criteria.where("date").gte(start));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (query.containsKey("endDate")) {
                String startDate = query.get("date").toString();
                try {
                    Date start = ServiceConstants.df.parse(startDate);
                    q.addCriteria(Criteria.where("date").lte(start));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if(pageable != null) {
            q.with(pageable);
        }
        return q;
    }
}
