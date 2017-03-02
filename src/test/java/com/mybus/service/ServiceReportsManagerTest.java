package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.dao.ServiceFormDAO;
import com.mybus.model.Booking;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import com.mybus.model.ServiceForm;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.*;
/**
 * Created by srinikandula on 2/20/17.
 */
public class ServiceReportsManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportsManager serviceReportsManager;

    @Autowired
    private ServiceFormDAO serviceFormDAO;

    @Before
    @After
    public void cleanup() {
        serviceFormDAO.deleteAll();
        serviceReportStatusDAO.deleteAll();
    }
    @Test
    public void testGetDownloadStatus() throws Exception {
        String date = "2017-02-01";
        ServiceReportStatus serviceReportStatus = new ServiceReportStatus();
        serviceReportStatus.setReportDate(AbhiBusPassengerReportService.df.parse(date));
        serviceReportStatusDAO.save(serviceReportStatus);
        JSONObject status = serviceReportsManager.getDownloadStatus(date);
        assertTrue(Boolean.valueOf(status.get("downloaded").toString()));
        status = serviceReportsManager.getDownloadStatus("2017-02-02");
        assertFalse(Boolean.valueOf(status.get("downloaded").toString()));
    }

    @Test
    public void testDownloadReport() throws Exception {

    }

    @Test
    public void testSubmitReport() throws ParseException {
        ServiceReport serviceReport = new ServiceReport();
        serviceReport.setJourneyDate(AbhiBusPassengerReportService.df.parse("2016-02-22"));
        serviceReport.setSource("Hyderabad");
        serviceReport.setDestination("Chennai");
        serviceReport.setBusType("Volvo");
        serviceReport.setVehicleRegNumber("AP 27 TU");
        serviceReport.setBookings(new ArrayList<>());
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setBookedBy(BookingTypeManager.REDBUS_CHANNEL);
            booking.setSeats("U"+i+",A"+i);
            booking.setNetAmt(2000);
            serviceReport.getBookings().add(booking);
        }
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setBookedBy(BookingTypeManager.ONLINE_CHANNEL);
            booking.setSeats("B"+i+",C"+i);
            booking.setNetAmt(1000);
            serviceReport.getBookings().add(booking);
        }
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setBookedBy(BookingTypeManager.CASH_CHANNEL);
            booking.setSeats("D"+i+",E"+i);
            booking.setNetAmt(2500);
            serviceReport.getBookings().add(booking);
        }
        serviceReport.setNetCashIncome(3000);
        ServiceForm report = serviceReportsManager.submitReport(serviceReport);
        Iterable<ServiceForm> serviceForms = serviceFormDAO.findAll();
        assertEquals(1, IteratorUtils.toList(serviceForms.iterator()).size());
        assertEquals(5, report.getBookings().size());
        assertTrue(3000 == report.getNetCashIncome());
        assertEquals(18, report.getSeatsCount());
        Iterable<ServiceReport> serviceReports = serviceReportDAO.findAll();
        assertEquals(1, IteratorUtils.toList(serviceReports.iterator()).size());
        ServiceReport report1 = serviceReports.iterator().next();
        assertEquals(report1.getAttributes().get(ServiceReport.SUBMITTED_ID), report.getId());

    }
}