package com.mybus.controller;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Booking;
import com.mybus.model.ServiceReport;
import com.mybus.model.User;
import com.mybus.service.AbhiBusPassengerReportService;
import com.mybus.service.ServiceConstants;
import com.mybus.service.ServiceReportsManager;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by srinikandula on 2/21/17.
 */
public class ServiceReportControllerTest extends AbstractControllerIntegrationTest {
    @Autowired
    private ServiceReportsManager serviceReportsManager;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BookingDAO bookingDAO;

    private User currentUser;

    @Before
    @After
    public void cleanup() {
        serviceReportDAO.deleteAll();
        bookingDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }
    private ServiceReport createTestData() throws Exception{
        ServiceReport report = new ServiceReport();
        report.setBusType("Sleeper");
        report.setJourneyDate(ServiceConstants.df.parse("2016-02-22"));
        report = serviceReportDAO.save(report);
        Set<Booking> bookingSet = new HashSet<>();
        for(int i=0;i<10;i++) {
            Booking booking = new Booking();
            booking.setName("testname"+i);
            booking.setServiceId(report.getId());
            booking.setBookedBy("skt-nlr");
            booking.setNetAmt(i+1);
            bookingDAO.save(booking);
        }
        return serviceReportDAO.save(report);
    }
    public void testGetDownloadStatus() throws Exception {

    }

    public void testDownloadReports() throws Exception {

    }

    public void testLoadReports() throws Exception {

    }
    @Test
    @Ignore
    public void testGetServiceReport() throws Exception {
        ServiceReport report = createTestData();
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/serviceReport/"+ report.getId()),
                currentUser));
        actions.andExpect(jsonPath("$").exists());
        actions.andExpect(jsonPath("$.busType").value("Sleeper"));
        actions.andExpect(jsonPath("$.bookings").isArray());
        actions.andExpect(jsonPath("$.bookings",Matchers.hasSize(10)));
        actions.andExpect(jsonPath("$.netRedbusIncome").value(0));
        actions.andExpect(jsonPath("$.netOnlineIncome").value(0));
        actions.andExpect(jsonPath("$.netCashIncome").value(55));
    }
    @Test
    public void testGetBooking() throws Exception {
        Booking booking = new Booking();
        booking.setBookedBy("BookedByTest");
        booking = bookingDAO.save(booking);
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/serviceReport/booking/"+booking.getId()), currentUser));
        actions.andExpect(jsonPath("$").exists());
        actions.andExpect(jsonPath("$.bookedBy").value("BookedByTest"));
    }
}