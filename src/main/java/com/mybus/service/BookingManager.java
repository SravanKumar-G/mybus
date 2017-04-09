package com.mybus.service;

import com.mongodb.BasicDBObject;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class BookingManager {
    private static final Logger logger = LoggerFactory.getLogger(BookingManager.class);

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private PaymentManager paymentManager;

    public boolean payBookingDue(String bookingId) {
        logger.debug("paying for booking :"+ bookingId);
        Booking booking = bookingDAO.findOne(bookingId);
        if(booking == null){
            throw new BadRequestException("No booking found with id");
        }
        if(!booking.isDue()){
            throw new BadRequestException("Booking has been paid off already");
        }
        if(booking.getFormId() == null){
            throw new BadRequestException("Wrong booking!! Only form bookings can be paid");
        }
        paymentManager.createPayment(booking);
        return bookingMongoDAO.markBookingPaid(bookingId);
    }


}
