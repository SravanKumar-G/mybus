package com.mybus.dao.impl;

import com.mongodb.BasicDBObject;
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by srinikandula on 3/3/17.
 */
public class BookingMongoDAOTest extends AbstractControllerIntegrationTest {

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Before
    @After
    public void cleanup() {
        bookingDAO.deleteAll();
        agentDAO.deleteAll();
    }

    @Test
    public void testFindDueBookingsByAgentNames() throws Exception {
        for(int a=0;a<5;a++) {
            Agent agent = new Agent();
            agent.setName("agent"+a);
            for(int i=0; i<5; i++) {
                Booking booking = new Booking();
                booking.setName("bookingName"+i);
                booking.setFormId("form"+a);
                booking.setBookedBy(agent.getName());
                if(i == 2) {
                    booking.setDue(true);
                }
                booking = bookingDAO.save(booking);
            }
            agentDAO.save(agent);
        }
        String[] agentNames = {"agent0", "agent1", "agent2", "agent34"};
        List<Booking> bookings = bookingMongoDAO.findDueBookings(Arrays.asList(agentNames), null);
        assertEquals(3, bookings.size());
    }

    @Test
    public void testMarkBookingPaid() throws Exception {
        String dueBookingId = null;
        for(int i=0; i<5; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            if(i == 2) {
                booking.setDue(true);
            }
            booking = bookingDAO.save(booking);
            if(i == 2) {
                dueBookingId = booking.getId();
            }
        }
        List<Booking> bookings = IteratorUtils.toList(bookingDAO.findByDue(false).iterator());
        assertEquals(4, bookings.size());
        boolean updated = bookingMongoDAO.markBookingPaid(dueBookingId);
        assertTrue("Update failed", updated);
        bookings = IteratorUtils.toList(bookingDAO.findByDue(false).iterator());
        assertEquals(5, bookings.size());
    }

    @Test
    public void testFindDueByService() {
        for(int i=0; i<21; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 2));
            booking.setNetAmt(200);
            booking.setDue(true);
            bookingDAO.save(booking);
        }
        List<BasicDBObject> dues = bookingMongoDAO.getBookingDueTotalsByService(null);
        assertEquals(2, dues.size());
        assertTrue(dues.get(0).get("_id").equals("ServiceNumber0"));
        assertTrue(Double.parseDouble(dues.get(0).get("totalDue").toString()) == 2200.0);
        assertTrue(dues.get(1).get("_id").equals("ServiceNumber1"));
        assertTrue(Double.parseDouble(dues.get(1).get("totalDue").toString()) == 2000.0);
    }

    @Test
    public void testFindOfficeDuesByService() {
        BranchOffice branchOffice1 = branchOfficeDAO.save(new BranchOffice());
        BranchOffice branchOffice2 = branchOfficeDAO.save(new BranchOffice());
        for(int i=0; i<21; i++) {
            Agent agent = new Agent();
            if(i%2 == 0) {
                agent.setBranchOfficeId(branchOffice1.getId());
            } else {
                agent.setBranchOfficeId(branchOffice2.getId());
            }
            agent.setUsername("agent" + i);
            agentDAO.save(agent);
        }
        for(int i=0; i<21; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.setBookedBy("agent"+i);
            bookingDAO.save(booking);
        }
        List<BasicDBObject> dues = bookingMongoDAO.getBookingDueTotalsByService(branchOffice1.getId());
        assertEquals(3, dues.size());
    }
}