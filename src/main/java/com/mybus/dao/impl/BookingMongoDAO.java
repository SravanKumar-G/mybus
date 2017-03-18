package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.dao.BookingDAO;
import com.mybus.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class BookingMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * Find due bookings by agent names and Journey Date
     * @param agentNames
     * @param JDate
     * @return
     */
    public List<Booking> findDueBookings(List<String> agentNames, String JDate) {
        final Query query = new Query();
        //query.fields().include("name");
        query.addCriteria(where("bookedBy").in(agentNames));
        query.addCriteria(where("due").is(true));
        if(JDate != null) {
            query.addCriteria(where("jDate").is(JDate));
        }
        query.addCriteria(where("formId").exists(true));
        query.addCriteria(where("serviceId").exists(false));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }
    public boolean markBookingPaid(String bookingId) {
        Update updateOp = new Update();
        updateOp.set("due", false);
        final Query query = new Query();
        query.addCriteria(where("_id").is(bookingId));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Booking.class);
        if(writeResult.getN() != 1) {
            return false;
        }
        return true;
    }

}
