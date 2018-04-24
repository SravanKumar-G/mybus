package com.mybus.dao.impl;


import com.mybus.model.CargoBooking;
import com.mybus.model.CashTransfer;
import com.mybus.model.User;
import com.mybus.service.BookingTypeManager;
import com.mybus.service.ServiceConstants;
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
import java.util.stream.Collectors;

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
        final Query q = createSearchQuery(query,pageable);
        List<CargoBooking> cargoBookings = mongoTemplate.find(q, CargoBooking.class);
        return cargoBookings;
    }

    private Query createSearchQuery(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query != null) {
            if(query.get("filter") != null) {
                q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(query.get("filter").toString(), "i"));
            }
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("fromBranchId") != null) {
                match.add(Criteria.where("fromBranchId").is(query.get("fromBranchId").toString()));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            if(query.get("bookedBy") != null) {
                match.add(Criteria.where("bookedBy").is(query.get("bookedBy").toString()));
            }
            if(query.get("deliveredBy") != null) {
                match.add(Criteria.where("deliveredBy").is(query.get("deliveredBy").toString()));
            }
            //match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

            if(match.size() > 0) {
                criteria.andOperator(match.toArray(new Criteria[match.size()]));
                q.addCriteria(criteria);
            }
        }

        return q;
    }

}
