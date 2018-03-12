package com.mybus.service;

import com.mybus.dao.*;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class ServiceReportsManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceReportsManager.class);
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private AbhiBusPassengerReportService abhiBusPassengerReportService;

    @Autowired
    private BitlaPassengerReportService bitlaPassengerReportService;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private ServiceFormDAO serviceFormDAO;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private BookingTypeManager bookingTypeManager;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ServiceExpenseManager serviceExpenseManager;

    @Autowired
    private ServiceListingManager serviceListingManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    public JSONObject getDownloadStatus(String date) throws ParseException {
        JSONObject response = new JSONObject();
        ServiceReportStatus status = serviceReportStatusDAO.findByReportDateBetweenAndOperatorId(
                ServiceUtils.parseDate(date, false),
                ServiceUtils.parseDate(date, true), sessionManager.getOperatorId());
        if(status != null) {
            response.put("downloaded", true);
            response.put("downloadedOn", dtf.print(status.getCreatedAt()));
        } else {
            response.put("downloaded", false);
        }
        return response;
    }

    public JSONObject downloadReports(String date) {
        OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }
        ServiceReportStatus status = null;
        if(operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)){
            status = abhiBusPassengerReportService.downloadReports(date);
        } else {
            status = bitlaPassengerReportService.downloadReports(date);
        }
        JSONObject response = new JSONObject();
        response.put("downloaded", true);
        response.put("downloadedOn", dtf.print(status.getCreatedAt()));
        return response;
    }
    
    public JSONObject getServicesByDate(String date) throws Exception {
        Iterable<ServiceListing> serviceListings = serviceListingManager.getServiceListings(date);
        JSONObject response = new JSONObject();
        response.put("downloaded", true);
        response.put("data", serviceListings);
        return response;
    }
    
    public JSONObject downloadServiceDetailsByNumberAndDate(String serviceNumber, String date) throws Exception {
        OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }
        List<ServiceReport> serviceReports = null;
        if(operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)){
            serviceReports = abhiBusPassengerReportService.getServiceDetailsByNumberAndDate(serviceNumber, date);
        }
        JSONObject response = new JSONObject();
        response.put("downloaded", true);
        response.put("data", serviceReports);
        //response.put("downloadedOn", dtf.print(status.getCreatedAt()));
        return response;
    }

    public List<ServiceReport> getReports(String date) throws IOException, ParseException {

        List<ServiceReport> reports = IteratorUtils.toList(
                serviceReportMongoDAO.findReports(date, null).iterator());
        serviceUtils.fillInUserNames(reports, ServiceReport.SUBMITTED_BY);
        return reports;
    }

    public ServiceReport getReport(String id) {
        ServiceReport report = serviceReportDAO.findOne(id);
        List<Booking> bookings = IteratorUtils.toList(bookingDAO.findByServiceId(report.getId()).iterator());
        //set the original cost for the old bookings
        if(report.getStatus() == null) {
            bookings.stream().forEach(booking ->
            {
                if (booking.getOriginalCost() == 0) {
                    booking.setOriginalCost(booking.getNetAmt());
                }
            });
        }
        Optional<Booking> invalidBooking = bookings.stream().filter(b -> !b.isHasValidAgent()).findFirst();
        if(invalidBooking.isPresent()) {
            report.setInvalid(true);
        } else {
            report.setInvalid(false);
        }
        /*
        report.setServiceExpense(serviceExpenseManager.getServiceExpenseByServiceReportId(report.getId()));
        */
        //round up the digits
        report.setNetRedbusIncome(roundUp(report.getNetRedbusIncome()));
        report.setNetOnlineIncome(roundUp(report.getNetOnlineIncome()));
        report.setNetCashIncome(roundUp(report.getNetCashIncome()));
        report.setNetIncome(roundUp(report.getNetCashIncome()+report.getNetOnlineIncome()+report.getNetRedbusIncome()));
        report.setBookings(bookings);
        List<Payment> expenses = paymentDAO.findByServiceReportId(report.getId());
        report.setExpenses(expenses);
        if(report.getUpdatedBy() != null) {
            report.getAttributes().put("updatedBy", userManager.getUser(report.getUpdatedBy()).getFullName());
        }
        return report;
    }



    private double roundUp(double value) {
        return (double) Math.round(value * 100) / 100;
    }

    /**
     * Submit the service report after the rates and expenses have been verified
     * @param serviceReport
     * @return
     */
    public ServiceForm submitReport(ServiceReport serviceReport) throws ParseException {
        logger.info("submitting the report");
        ServiceForm serviceForm = new ServiceForm();
        serviceForm.setOperatorId(sessionManager.getOperatorId());
        serviceReport.setNetCashIncome(0);
        //need to set this for update balance of the submitted user but not the verified user
        serviceForm.setSubmittedBy(serviceReport.getSubmittedBy());
        serviceForm.setServiceReportId(serviceReport.getId());
        serviceForm.setServiceNumber(serviceReport.getServiceNumber());
        serviceForm.setServiceName(serviceReport.getServiceName());
        ServiceReport savedReport = serviceReportDAO.findOne(serviceReport.getId());
        if(savedReport.getStatus() != null && savedReport.getStatus().equals(ServiceStatus.SUBMITTED)) {
            throw new BadRequestException("Oops, looks like the report has been already submitted");
        }
        //save the service expense
        ServiceExpense serviceExpense = serviceReport.getServiceExpense();
        serviceExpenseManager.updateFromServiceReport(serviceExpense);
        if(serviceReport.getStatus() != null && serviceReport.getStatus().equals(ServiceStatus.SUBMITTED)) {
            Map<String, List<String>> seatBookings = new HashMap<>();
            Booking redbusBooking = new Booking();
            redbusBooking.setBookedBy(BookingTypeManager.REDBUS_CHANNEL);
            Booking onlineBooking = new Booking();
            onlineBooking.setBookedBy(BookingTypeManager.ONLINE_CHANNEL);
            double cashIncome = 0;
            for (Booking booking : serviceReport.getBookings()) {
                //Need to set journey date for service bookings
                if (booking.getJourneyDate() == null) {
                    booking.setJourneyDate(serviceReport.getJourneyDate());
                    booking.setJDate(ServiceConstants.formatDate(serviceReport.getJourneyDate()));
                    booking.setDestination(serviceReport.getDestination());
                    booking.setSource(serviceReport.getSource());
                    booking.setServiceName(serviceReport.getServiceName());
                    booking.setServiceNumber(serviceReport.getServiceNumber());
                }
                if (bookingTypeManager.isRedbusBooking(booking)) {
                    processBooking(seatBookings, redbusBooking, booking);
                } else if (bookingTypeManager.isOnlineBooking(booking)) {
                    processBooking(seatBookings, onlineBooking, booking);
                } else {
                    //add dues based on agent
                    booking.setId(null);
                    if (!booking.isDue()) {
                        Agent bookingAgent = bookingTypeManager.getBookingAgent(booking);
                        booking.setHasValidAgent(bookingAgent != null);
                        //adjustAgentBookingCommission(booking, bookingAgent);
                        serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
                    }
                /*
                if(!booking.isDue()){
                    if(!booking.isHasValidAgent()) {
                        throw new BadRequestException("Invalid Agent found "+ booking.getBookedBy());
                    }
                    Agent agent = agentDAO.findByUsername(booking.getBookedBy());
                    if(agent != null){
                        //TODO add check
                        transactionManager.createBookingTransaction(booking, agent);
                        boolean updated = branchOfficeMongoDAO.updateCashBalance(agent.getBranchOfficeId(), booking.getNetAmt());
                        if(!updated) {
                            throw new BadRequestException("Updating cash balanace failed for agent "+ booking.getBookedBy());
                        }
                    }
                }
                */
                    booking.setServiceId(null);
                    serviceForm.getBookings().add(booking);
                    if (booking.getSeats() != null) {
                        String[] seats = booking.getSeats().split(",");
                        booking.setSeatsCount(seats.length);
                        serviceForm.setSeatsCount(serviceForm.getSeatsCount() + seats.length);
                    }
                    cashIncome += booking.getNetAmt();
                }
            }

            if (redbusBooking.getSeatsCount() > 0) {
                redbusBooking.setNetAmt(roundUp(redbusBooking.getNetAmt()));
                serviceForm.getBookings().add(redbusBooking);
                serviceForm.setSeatsCount(serviceForm.getSeatsCount() + redbusBooking.getSeatsCount());
            }
            if (onlineBooking.getSeatsCount() > 0) {
                onlineBooking.setNetAmt(roundUp(onlineBooking.getNetAmt()));
                serviceForm.getBookings().add(onlineBooking);
                serviceForm.setSeatsCount(serviceForm.getSeatsCount() + onlineBooking.getSeatsCount());
            }
            for (Payment expense : serviceReport.getExpenses()) {
                serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() - expense.getAmount());
            }
            serviceForm.setNetRedbusIncome(serviceReport.getNetRedbusIncome());
            serviceForm.setNetOnlineIncome(serviceReport.getNetOnlineIncome());
            serviceForm.setNetCashIncome(serviceReport.getNetCashIncome());
            serviceForm.setNetIncome(serviceReport.getNetRedbusIncome() + serviceReport.getNetOnlineIncome() + cashIncome);
            serviceForm.setSource(serviceReport.getSource());
            serviceForm.setDestination(serviceReport.getDestination());
            serviceForm.setBusType(serviceReport.getBusType());
            serviceForm.setVehicleRegNumber(serviceReport.getVehicleRegNumber());
            serviceForm.setJDate(serviceReport.getJourneyDate());
            serviceForm.setConductorInfo(serviceReport.getConductorInfo());
            serviceForm.setNotes(serviceReport.getNotes());
            ServiceForm savedForm = serviceFormDAO.save(serviceForm);
            paymentManager.createPayment(serviceForm, false);
            serviceForm.getBookings().stream().forEach(booking -> {
                booking.setFormId(savedForm.getId());
            });
            serviceReport.getExpenses().stream().forEach(expense -> {
                expense.setFormId(savedForm.getId());
            });
            paymentDAO.save(serviceReport.getExpenses());
            bookingDAO.save(serviceForm.getBookings());
            serviceReport.getAttributes().put(ServiceReport.SUBMITTED_ID, savedForm.getId());
        }
        serviceReportDAO.save(serviceReport);
        return serviceForm;
    }

    /**
     * Save the serviceReport for ticket cost verification
     * @param serviceReport
     */
    public ServiceReport saveServiceReportForVerification(ServiceReport serviceReport) {
        serviceReport.setStatus(ServiceStatus.REQUIRE_VERIFICATION);
        //incase if any service booking added in the frontend
        serviceReport.getBookings().stream().forEach(booking -> booking.setServiceId(serviceReport.getId()));
        bookingDAO.save(serviceReport.getBookings());
        //save the expenses, reload them when the service report is re-loaded
        serviceReport.getExpenses().stream().forEach(expense -> {
            expense.setServiceReportId(serviceReport.getId());
        });
        paymentDAO.save(serviceReport.getExpenses());
        return serviceReportDAO.save(serviceReport);
    }
    private void processBooking(Map<String, List<String>> seatBookings, Booking consolidation, Booking booking) {
        if(bookingTypeManager.isRedbusBooking(booking)) {
            if (seatBookings.get(BookingTypeManager.REDBUS_CHANNEL) == null) {
                seatBookings.put(BookingTypeManager.REDBUS_CHANNEL, new ArrayList<>());
            }
        } else  if(bookingTypeManager.isOnlineBooking(booking)){
            if (seatBookings.get(BookingTypeManager.ONLINE_CHANNEL) == null) {
                seatBookings.put(BookingTypeManager.ONLINE_CHANNEL, new ArrayList<>());
            }
        }
        String[] seats = booking.getSeats().split(",");
        if(consolidation.getSeats() == null) {
            consolidation.setSeats(booking.getSeats());
        } else {
            consolidation.setSeats(consolidation.getSeats() +","+booking.getSeats());
        }
        consolidation.setSeatsCount(consolidation.getSeatsCount()+seats.length);
        consolidation.setNetAmt(consolidation.getNetAmt() + booking.getNetAmt());
    }

    public ServiceForm getForm(String id) {
        ServiceForm serviceForm = serviceFormDAO.findOne(id);
        Iterable<Booking> bookings = bookingDAO.findByFormId(serviceForm.getId());
        /*
        //load the service expense
        serviceForm.setServiceExpense(serviceExpenseManager.getServiceExpenseByServiceReportId(serviceForm.getServiceReportId()));
        */
        //round up the digits
        serviceForm.setNetRedbusIncome(roundUp(serviceForm.getNetRedbusIncome()));
        serviceForm.setNetOnlineIncome(roundUp(serviceForm.getNetOnlineIncome()));
        serviceForm.setNetCashIncome(roundUp(serviceForm.getNetCashIncome()));
        serviceForm.setNetIncome(roundUp(serviceForm.getNetIncome()));
        serviceForm.setExpenses(IteratorUtils.toList(paymentDAO.findByFormId(id).iterator()));
        serviceForm.setBookings(IteratorUtils.toList(bookings.iterator()));
        if(serviceForm.getSubmittedBy() != null) {
            serviceForm.getAttributes().put("submittedBy", userManager.getUser(serviceForm.getSubmittedBy()).getFullName());
        } else {
            serviceForm.getAttributes().put("submittedBy", userManager.getUser(serviceForm.getCreatedBy()).getFullName());
        }
        return serviceForm;
    }

    public void clearServiceReports(Date date, OperatorAccount operatorAccount) throws ParseException {
        JSONObject query = new JSONObject();
        query.put(ServiceReport.JOURNEY_DATE, date);
        if(operatorAccount != null) {
            query.put(SessionManager.OPERATOR_ID, operatorAccount.getOperatorId());
        }
        Iterable<ServiceReport> serviceReports = serviceReportMongoDAO.findReports(ServiceConstants.formatDate(date), null);
        serviceReports.forEach(serviceReport -> {
            if (serviceReport.getAttributes().containsKey(ServiceReport.SUBMITTED_ID)) {
                String formId = serviceReport.getAttributes().get(ServiceReport.SUBMITTED_ID);
                ServiceForm serviceForm = serviceFormDAO.findOne(formId);
                paymentManager.createPayment(serviceForm, true);
                serviceFormDAO.delete(serviceForm);
                bookingDAO.deleteByFormId(formId);
            }
            serviceReportDAO.delete(serviceReport);
            bookingDAO.deleteByServiceId(serviceReport.getId());
        });
        serviceReportStatusDAO.deleteByReportDate(date);
        //serviceReportStatusDAO.save(new ServiceReportStatus(date, ReportDownloadStatus.DOWNLOADING));
    }

    public Iterable<ServiceReport> refreshReport(Date date) throws ParseException {
        OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }

        clearServiceReports(date, operatorAccount);
        try {
            if(operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)){
                abhiBusPassengerReportService.downloadReports(ServiceConstants.formatDate(date));
            } else {
                bitlaPassengerReportService.downloadReports(ServiceConstants.formatDate(date));
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to download reports", e);
        }
        return serviceReportDAO.findByJourneyDate(date);
    }

    public Booking getBooking(String bookingId) {
        return bookingDAO.findOne(bookingId);
    }

    public Invoice findBookingsInvoice(JSONObject query) {
        Invoice invoice = null;
        try {
            invoice = bookingMongoDAO.findBookingsInvoice(
                    ServiceUtils.parseDate(query.get("startDate").toString(), false),
                    ServiceUtils.parseDate(query.get("endDate").toString(), true), ((List<String>)query.get("channel")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return invoice;
    }

}
