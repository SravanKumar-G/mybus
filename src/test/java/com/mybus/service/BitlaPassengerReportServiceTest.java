package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BitlaPassengerReportServiceTest  extends AbstractControllerIntegrationTest {

    @Autowired
    private BitlaPassengerReportService bitlaPassengerReportService;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Before
    @After
    public void cleanup() {
        serviceReportDAO.deleteAll();
        serviceReportStatusDAO.deleteAll();
        bookingDAO.deleteAll();
    }

    @Test
    public void testDownloadReports() {
        bitlaPassengerReportService.downloadReports("2018-02-16");
    }
}