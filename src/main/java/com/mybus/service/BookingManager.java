package com.mybus.service;

import com.google.gson.JsonObject;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.ServiceReport;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Autowired
    private SessionManager sessionManager;

    /**
     * Pay collection of bookings
     * @param bookingIds
     * @return
     */
    public  List<Booking> payBookingDues(List<String> bookingIds) {
        List<Booking> bookings = bookingMongoDAO.findBookingsByIds(bookingIds);
        bookings.stream().forEach(booking -> {
            if(payBookingDue(booking.getId())){
                booking.setPaidOn(new Date());
                booking.setPaidBy(sessionManager.getCurrentUser().getId());
            }
        });
        return bookings;
    }

    public boolean payBookingDue(String bookingId) {
        Booking booking = bookingDAO.findOne(bookingId);
        return payBooking(bookingId, booking);
    }

    private boolean payBooking(String bookingId, Booking booking) {
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

    /**
     * validates all the bookings for an agent. This is triggered when agent is allotted to an office.
     * This will update the bookings made by that agent and service reports containing the bookings.
     * @param agent
     */
    public void validateAgentBookings(Agent agent) {
        if(agent.getBranchOfficeId() == null) {
            return;
        }
        Iterable<Booking> bookings = bookingDAO.findByBookedByAndHasValidAgent(agent.getUsername(), false);
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


    public List<Booking> getBookingsByPhone(String phoneNumber) {
        return bookingDAO.findByPhoneNo(phoneNumber);
    }
}
