package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Booking;
import com.mybus.model.User;
import com.mybus.test.util.UserTestService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class BookingManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private BookingManager bookingManager;

    private User currentUser;
    @Before
    @After
    public void cleanup(){
        userDAO.deleteAll();
        bookingDAO.deleteAll();
        currentUser = userDAO.save(UserTestService.createNew());
        sessionManager.setCurrentUser(currentUser);
    }
    @Test
    public void testPayBookingDues() {
        List<String> bookingIds = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Booking booking = new Booking();
            booking.setFormId("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.setBookedBy("agent" + i);
            booking = bookingDAO.save(booking);
            bookingIds.add(booking.getId());
        }
        List<Booking> bookings = bookingManager.payBookingDues(bookingIds);
        assertEquals(bookings.size(), 10, 0.0);
        bookings.stream().forEach(booking -> {
            assertNotNull(booking.getPaidOn());
        });
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(currentUser.getAmountToBePaid(), 2000, 0.0);
    }
}