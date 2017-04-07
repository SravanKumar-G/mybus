package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.BranchOfficeDue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Created by srinikandula on 3/3/17.
 */
public class DueReportManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private DueReportManager dueReportManager;

    @Before
    @After
    public void cleanup() {
        bookingDAO.deleteAll();
        agentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
    }

    @Test
    public void testGetBranchOfficeDueReports() throws Exception {
        List<BranchOffice> offices = new ArrayList<>();
        List<Agent> agents = new ArrayList<>();

        for(int i=0; i<5; i++) {
            BranchOffice office = new BranchOffice();
            office.setName("office" +i);
            offices.add(branchOfficeDAO.save(office));
        }
        for(int i=0; i<5; i++) {
            Agent agent = new Agent();
            agent.setName("Agent"+i);
            agent.setUsername("AgentName"+i);
            agent.setBranchOfficeId(offices.get(i).getId());
            agents.add(agentDAO.save(agent));
        }
        for(int i=0; i<53; i++) {
            int agentIndex = i%5;
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setFormId("form"+i);
            booking.setBookedBy(agents.get(agentIndex).getUsername());
            booking.setNetAmt(200);
            if(i%2 == 0) {
                booking.setDue(true);
            }
            booking = bookingDAO.save(booking);
        }
        List<BranchOfficeDue> dues = dueReportManager.getBranchOfficesDueReports();
        assertEquals(5, dues.size());
        dues.stream().forEach(due -> {
            assertTrue(due.getBookings() == null);
        });
        BranchOfficeDue officeDue = dueReportManager.getBranchOfficeDueReport(dues.get(0).getBranchOfficeId());
        assertTrue(!officeDue.getBookings().isEmpty());
    }

}