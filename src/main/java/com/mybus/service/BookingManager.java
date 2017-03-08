package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
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
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private BranchOfficeMongoDAO branchOfficeMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private AgentDAO agentDAO;

    public boolean payBookingDue(String bookingId) {
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
        Agent agent = agentDAO.findByUsername(booking.getBookedBy());
        branchOfficeMongoDAO.updateCashBalance(agent.getBranchOfficeId(), booking.getNetAmt());
        return bookingMongoDAO.markBookingPaid(bookingId);
    }

}
