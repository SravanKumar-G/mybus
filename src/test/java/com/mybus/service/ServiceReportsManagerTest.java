package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.*;
import com.mybus.model.*;
import com.mybus.test.util.UserTestService;
import com.mybus.util.ServiceConstants;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DueReportManager dueReportManager;

    @Autowired
    private BookingManager bookingManager;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Before
    @After
    public void cleanup() {
        agentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
        bookingDAO.deleteAll();
        serviceFormDAO.deleteAll();
        serviceReportDAO.deleteAll();
        serviceReportStatusDAO.deleteAll();
        paymentDAO.deleteAll();
        userDAO.deleteAll();
    }
    @Test
    @Ignore
    public void testGetDownloadStatus() throws Exception {
        String date = "2017-02-22";
        ServiceReportStatus serviceReportStatus = new ServiceReportStatus();
        serviceReportStatus.setReportDate(ServiceUtils.parseDate(date, false));
        serviceReportStatus.setOperatorId(null);
        serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
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
    public void testUpdateOfficeBalances() throws ParseException {
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount(OperatorAccount.ABHIBUS));

        User user = new User();
        BranchOffice office1 = new BranchOffice();
        office1.setName("Office1");
        office1 = branchOfficeDAO.save(office1);

        BranchOffice office2 = new BranchOffice();
        office2.setName("Office2");
        office2 = branchOfficeDAO.save(office2);

        user.setBranchOfficeId(office2.getId());
        user = userDAO.save(user);
        sessionManager.setCurrentUser(user);
        Agent agent1 = new Agent();
        agent1.setName("TestAgent1");
        agent1.setUsername("TestAgent1");
        agent1.setBranchOfficeId(office1.getId());
        agent1 = agentDAO.save(agent1);

        Agent agent2 = new Agent();
        agent2.setName("TestAgent2");
        agent2.setUsername("TestAgent2");
        agent2.setBranchOfficeId(office2.getId());
        agent2 = agentDAO.save(agent2);
        ServiceReport serviceReport = new ServiceReport();
        serviceReport.setJourneyDate(new Date());
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setBookedBy(agent2.getUsername());
            booking.setSeats("D"+i+",E"+i);
            booking.setNetAmt(2500);
            booking.setPaymentType(BookingType.CASH);
            if(i ==2){
                booking.setDue(true);
            } else {
                serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            }
            booking.setBookedBy(agent2.getName());
            serviceReport.getBookings().add(bookingDAO.save(booking));
        }
        serviceReport = serviceReportDAO.save(serviceReport);
        serviceReport.setStatus(ServiceStatus.SUBMITTED);
        serviceReport.setOperatorId(operatorAccount.getId());
        serviceReportsManager.submitReport(serviceReport);
        List<Booking> bookings = IteratorUtils.toList(bookingDAO.findAll().iterator());
        assertEquals(6, bookings.size());
        user = userDAO.findById(user.getId()).get();
        assertEquals(5000, user.getAmountToBePaid(), 0.0);

        List<Payment> payments = IteratorUtils.toList(paymentDAO.findAll().iterator());
        assertEquals(1, payments.size());
        assertEquals(payments.get(0).getType(), PaymentType.INCOME);
        assertEquals(5000, payments.get(0).getAmount(), 0.0);

        List<BranchOfficeDue> officeDues = dueReportManager.getBranchOfficesDueReports();
        assertEquals(2, officeDues.size());
        officeDues.stream().forEach(office -> {
            if(office.getName().equals("Office2")) {
                assertEquals(2500, office.getTotalDue(),0.0);
            }
        });
        for(Booking booking: bookings){
            if(booking.isDue() && booking.getFormId() != null) {
                boolean paid = bookingManager.payBookingDue(booking.getId());
                assertTrue("payment of the booking failed", paid);
                break;
            }
        }
        officeDues = dueReportManager.getBranchOfficesDueReports();
        assertEquals(2, officeDues.size());
        final User currentUser = userDAO.findById(user.getId()).get();

        officeDues.stream().forEach(office -> {
            if(office.getName().equals("Office2")) {
                assertEquals(0, office.getTotalDue(),0.0);
                assertEquals(0, office.getCashBalance(),0.0);
                assertEquals(7500, currentUser.getAmountToBePaid(), 0.0);
            }
        });
        payments = IteratorUtils.toList(paymentDAO.findAll().iterator());
        assertEquals(2, payments.size());
        payments.stream().forEach(payment -> {
            assertEquals(payment.getType(), PaymentType.INCOME);
        });
    }
    @Ignore
    @Test
    public void testRefreshServiceReport() throws ParseException {
        User user = UserTestService.createNew();
        BranchOffice office = new BranchOffice();
        office = branchOfficeDAO.save(office);
        user.setBranchOfficeId(office.getId());
        user = userDAO.save(user);
        sessionManager.setCurrentUser(user);
        Calendar calendar = Calendar.getInstance();
        List<String> reportIds = new ArrayList<>();
        for(int i=0; i<3; i++) {
            ServiceReport report = new ServiceReport();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            report.setJourneyDate(ServiceConstants.parseDate(ServiceConstants.formatDate(calendar.getTime())));
            report.setServiceNumber("Service"+i);
            report = serviceReportDAO.save(report);
            for(int b=0;b<5;b++) {
                Booking booking = new Booking();
                booking.setServiceReportId(report.getId());
                booking.setNetAmt(100);
                if(b == 3){
                    booking.setDue(true);
                }
                report.getBookings().add(bookingDAO.save(booking));
            }
            report.setStatus(ServiceStatus.SUBMITTED);

            //add service expense
            /*
            ServiceExpense serviceExpense = new ServiceExpense(report);
            serviceExpense.setToPayLuggage(100);
            serviceExpense.setPaidLuggage(100);
            serviceExpense.setDriverSalary1(100);
            report.setServiceExpense(serviceExpenseManager.save(serviceExpense));
            */
            serviceReportsManager.submitReport(report);
            reportIds.add(report.getId());
        }
        user = userDAO.findById(user.getId()).get();
        assertEquals(1200, user.getAmountToBePaid(), 0.0);
        //serviceReportsManager.clearServiceReports(ServiceConstants.parseDate(new Date()), null);
        user = userDAO.findById(user.getId()).get();
        assertEquals(800, user.getAmountToBePaid(), 0.0);
        List<ServiceReport> reports = IteratorUtils.toList(serviceReportDAO.findAll().iterator());
        assertEquals(2, reports.size());
        List<Booking> bookings = IteratorUtils.toList(bookingDAO.findAll().iterator());
        assertEquals(20, bookings.size());
        /*for(ServiceReport report : reports) {
            ServiceReport serviceReport = serviceReportsManager.getReport(report.getId());
            assertNotNull(serviceReport.getServiceExpense());
            assertEquals(100, serviceReport.getServiceExpense().getToPayLuggage(), 0.0);
            assertEquals(100, serviceReport.getServiceExpense().getPaidLuggage(), 0.0);
            assertEquals(100, serviceReport.getServiceExpense().getDriverSalary1(), 0.0);
            assertEquals(0, serviceReport.getServiceExpense().getDriverSalary2(), 0.0);
        }*/

    }


    @Test
    public void testFormSubmit() throws ParseException {
        ServiceReport serviceReport = new ServiceReport();
        serviceReport.setJourneyDate(new Date());
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setSeats("D"+i+",E"+i);
            booking.setNetAmt(2500);
            booking.setBookedBy("test");
            booking.setPaymentType(BookingType.CASH);
            if(i ==2){
                booking.setDue(true);
            } else {
                serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            }
            serviceReport.getBookings().add(bookingDAO.save(booking));
        }
        serviceReport = serviceReportDAO.save(serviceReport);
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount(OperatorAccount.ABHIBUS));
        User user = userDAO.save(UserTestService.createNew());
        sessionManager.setCurrentUser(user);
        serviceReport.setStatus(ServiceStatus.SUBMITTED);
        serviceReport.setOperatorId(operatorAccount.getId());
        serviceReportsManager.submitReport(serviceReport);
        user = userDAO.findById(user.getId()).get();
        assertEquals(5000, user.getAmountToBePaid(), 0.0);
        User verifyingUser = userDAO.save(UserTestService.createNew());
    }


    @Test
    public void testFormVerification() throws ParseException {
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount(OperatorAccount.ABHIBUS));
        ServiceReport serviceReport = new ServiceReport();
        serviceReport.setJourneyDate(new Date());
        for(int i=0; i<3; i++) {
            Booking booking = new Booking();
            booking.setSeats("D"+i+",E"+i);
            booking.setNetAmt(2500);
            booking.setBookedBy("test");
            booking.setPaymentType(BookingType.CASH);
            if(i ==2){
                booking.setDue(true);
            } else {
                serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            }
            serviceReport.getBookings().add(bookingDAO.save(booking));
        }
        serviceReport = serviceReportDAO.save(serviceReport);
        User user = userDAO.save(UserTestService.createNew());
        serviceReport.setStatus(ServiceStatus.SUBMITTED);
        serviceReport.setSubmittedBy(user.getId());
        User verifyingUser = userDAO.save(UserTestService.createNew());
        sessionManager.setCurrentUser(verifyingUser);
        serviceReport.setOperatorId(operatorAccount.getId());
        serviceReportsManager.submitReport(serviceReport);
        user = userDAO.findById(user.getId()).get();
        verifyingUser = userDAO.findById(verifyingUser.getId()).get();
        assertEquals(5000, user.getAmountToBePaid(), 0.0);
        assertEquals(0, verifyingUser.getAmountToBePaid(), 0.0);
    }


}