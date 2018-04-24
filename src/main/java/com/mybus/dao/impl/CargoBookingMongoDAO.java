package com.mybus.dao.impl;


import com.mybus.model.CargoBooking;
import com.mybus.model.CashTransfer;
import com.mybus.service.BookingTypeManager;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by srinikandula on 12/11/16.
 */
@Repository
public class CargoBookingMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public List<CargoBooking> findShipments(JSONObject query, final Pageable pageable) throws ParseException {
        final Query q = new Query();
        if(query != null) {
            if(query.get(CargoBooking.DISPATCH_DATE) != null ){
                Date start = ServiceUtils.parseDate(query.get(CargoBooking.DISPATCH_DATE).toString(), false);
                Date end = ServiceUtils.parseDate(query.get(CargoBooking.DISPATCH_DATE).toString(), true);
                q.addCriteria(where(CargoBooking.DISPATCH_DATE).gte(start).lte(end));
            }
            if(query.get("filter") != null) {
                q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(query.get("filter").toString(), "i"));
            }
        }
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

        List<CargoBooking> cargoBookings = mongoTemplate.find(q, CargoBooking.class);
        return cargoBookings;
    }

}
