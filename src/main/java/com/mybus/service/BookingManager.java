package com.mybus.service;

import com.mongodb.BasicDBObject;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.PaymentType;
import com.mybus.model.ServiceReport;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private ServiceReportDAO serviceReportDAO;

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

    public void validateAgentBookings(String agentName) {
        Iterable<Booking> bookings = bookingDAO.findByBookedByAndHasValidAgent(agentName, false);
        Set<String> serviceNumbers = new HashSet<>();
        for(Booking booking: bookings) {
            serviceNumbers.add(booking.getServiceId());
            booking.setHasValidAgent(true);
            bookingDAO.save(booking);
        }
        for(String serviceId: serviceNumbers) {
            List<Booking> invalidBookings = IteratorUtils.toList(
                    bookingDAO.findByIdAndHasValidAgent(serviceId, false).iterator());
            if(invalidBookings.size() == 0) {
                ServiceReport serviceReport = serviceReportDAO.findOne(serviceId);
                if(serviceReport.isInvalid()) {
                    serviceReport.setInvalid(false);
                    serviceReportDAO.save(serviceReport);
                }
            }
        }
    }


}
