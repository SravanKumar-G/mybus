package com.mybus.controller;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.User;
import com.mybus.service.DueReportManager;
import com.mybus.service.ServiceConstants;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 4/9/17.
 */
public class DueReportControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    private MockMvc mockMvc;

    @Before
    @After
    public void cleanup() {
        bookingDAO.deleteAll();
        branchOfficeDAO.deleteAll();
        agentDAO.deleteAll();
        userDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }

    @Test
    public void testGetDueBookingsByService() throws Exception {
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
            booking.setFormId("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.setBookedBy("agent" + i);
            bookingDAO.save(booking);
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/dueReport/dueBookingByService/ServiceNumber0"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(7)));

        actions = mockMvc.perform(asUser(get("/api/v1/dueReport/dueBookingByService/ServiceNumber6"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
    @Test
    public void testSearchDues() throws Exception {
        BranchOffice branchOffice1 = branchOfficeDAO.save(new BranchOffice());
        BranchOffice branchOffice2 = branchOfficeDAO.save(new BranchOffice());
        createTestAgents(branchOffice1, branchOffice2);
        createdTestBookings();
        Calendar calendar = Calendar.getInstance();
        String endDate = ServiceConstants.df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -2);
        String startDate = ServiceConstants.df.format(calendar.getTime());
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/dueReport/search?startDate="+startDate+"&endDate="+endDate), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(6)));


        endDate = ServiceConstants.df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -6);
        startDate = ServiceConstants.df.format(calendar.getTime());

        actions = mockMvc.perform(asUser(get("/api/v1/dueReport/search?startDate="+startDate+"&endDate="+endDate+"&branchOfficeId="+branchOffice2.getId()), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(7)));


    }
}