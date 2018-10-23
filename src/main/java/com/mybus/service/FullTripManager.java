package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.FullTripsDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.FullTripMongoDAO;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by srinikandula on 12/12/16.
 */

@Service
public class FullTripManager {
    private static final Logger logger = LoggerFactory.getLogger(FullTripManager.class);

    @Autowired
    private FullTripsDAO fullTripsDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private UserManager userManager;

    @Autowired
    private FullTripMongoDAO fullTripMongoDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private PaymentManager paymentManager;

    public FullTrip updateFullTrip(FullTrip fullTrip) {
        if(fullTrip.getId() == null) {
            throw new IllegalArgumentException("Invalid Id in FullTrip");
        }
        //if the payment is already paid, don't update
        if (!fullTrip.isDue()) {
            throw new IllegalArgumentException("FullTrip is already paid");
        }
        FullTrip savedTrip = fullTripsDAO.findById(fullTrip.getId()).get();
        savedTrip.setFrom(fullTrip.getFrom());
        savedTrip.setTo(fullTrip.getTo());
        savedTrip.setCharge(fullTrip.getCharge());
        savedTrip.setContact(fullTrip.getContact());
        savedTrip.setTripDate(fullTrip.getTripDate());
        return savedTrip;
    }

    public FullTrip save(FullTrip fullTrip) {
        fullTrip.validate();
        fullTrip.setOperatorId(sessionManager.getOperatorId());
        return fullTripsDAO.save(fullTrip);
    }
    public boolean payOffFullTrip(String fullTripId){
        FullTrip fullTrip = fullTripsDAO.findById(fullTripId).get();
        if(fullTrip.getId() == null) {
            throw new IllegalArgumentException("Invalid Id in FullTrip");
        }
        //if the payment is already paid, don't update
        if (!fullTrip.isDue()) {
            throw new IllegalArgumentException("FullTrip is already paid");
        }
        paymentManager.createPayment(fullTrip);
        return true;
    }

    public Page<FullTrip> search(JSONObject query, Pageable pageable) throws ParseException {
        return fullTripMongoDAO.search(query, pageable);
    }

    public FullTrip findOne(String id) {
        return fullTripsDAO.findById(id).get();
    }

    public long count(JSONObject query) throws ParseException {
        return fullTripMongoDAO.count(query);
    }

    public void delete(String id) {
        fullTripsDAO.deleteById(id);
    }
}
