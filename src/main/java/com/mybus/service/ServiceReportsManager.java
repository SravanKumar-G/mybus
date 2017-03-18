package com.mybus.service;

import com.mybus.dao.*;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.model.*;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AbhiBusPassengerReportService reportService;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private ServiceFormDAO serviceFormDAO;


    @Autowired
    private BookingTypeManager bookingTypeManager;

    @Autowired
    private PaymentManager paymentManager;

    public JSONObject getDownloadStatus(String date) throws ParseException {
        JSONObject response = new JSONObject();
        Iterator<ServiceReportStatus> statusIterator = serviceReportStatusDAO
                .findByReportDate(ServiceConstants.df.parse(date)).iterator();
        if(statusIterator.hasNext()) {
            response.put("downloaded", true);
            response.put("downloadedOn", dtf.print(statusIterator.next().getCreatedAt()));
        } else {
            response.put("downloaded", false);
        }
        return response;
    }

    public JSONObject downloadReport(String date) throws Exception {
        ServiceReportStatus status = reportService.downloadReport(date);
        JSONObject response = new JSONObject();
        response.put("downloaded", true);
        response.put("downloadedOn", dtf.print(status.getCreatedAt()));
        return response;
    }

    public Iterable<ServiceReport> getReports(Date date) {
        JSONObject query = new JSONObject();
        query.put(ServiceReport.JOURNEY_DATE, date);
        return serviceReportMongoDAO.findReports(query, null);
    }

    public ServiceReport getReport(String id) {
        ServiceReport report = serviceReportDAO.findOne(id);
        Iterable<Booking> bookings = bookingDAO.findByServiceId(report.getId());
        for(Booking booking:bookings) {
            if(bookingTypeManager.isRedbusBooking(booking)){
                report.setNetRedbusIncome(report.getNetRedbusIncome() + booking.getNetAmt());
                booking.setPaymentType(BookingType.REDBUS);
            } else if(bookingTypeManager.isOnlineBooking(booking)) {
                report.setNetOnlineIncome(report.getNetOnlineIncome() + booking.getNetAmt());
                booking.setPaymentType(BookingType.ONLINE);
            } else {
                report.setNetCashIncome(report.getNetCashIncome() + booking.getNetAmt());
                booking.setPaymentType(BookingType.CASH);
                booking.setHasValidAgent(bookingTypeManager.hasValidAgent(booking));
                if(!booking.isHasValidAgent() && !report.isInvalid()) {
                    report.setInvalid(true);
                }
            }
        }
        //round up the digits
        report.setNetRedbusIncome(roundUp(report.getNetRedbusIncome()));
        report.setNetOnlineIncome(roundUp(report.getNetOnlineIncome()));
        report.setNetCashIncome(roundUp(report.getNetCashIncome()));
        report.setNetIncome(roundUp(report.getNetCashIncome()+report.getNetOnlineIncome()+report.getNetRedbusIncome()));
        report.setBookings(IteratorUtils.toList(bookings.iterator()));
        return report;
    }
    private double roundUp(double value) {
        return (double) Math.round(value * 100) / 100;
    }

    public ServiceForm submitReport(ServiceReport serviceReport) {
        logger.info("submitting the report");
        ServiceForm serviceForm = new ServiceForm();
        serviceReport.setNetCashIncome(0);
        serviceForm.setServiceReportId(serviceReport.getId());
        Map<String, Double> officeCashBalances = new HashMap<>();
        Map<String,List<String>> seatBookings = new HashMap<>();
        Booking redbusBooking = new Booking();
        redbusBooking.setBookedBy(BookingTypeManager.REDBUS_CHANNEL);
        Booking onlineBooking = new Booking();
        onlineBooking.setBookedBy(BookingTypeManager.ONLINE_CHANNEL);
        double cashIncome = 0;
        for(Booking booking: serviceReport.getBookings()) {
            if(bookingTypeManager.isRedbusBooking(booking) ){
                processBooking(seatBookings, redbusBooking, booking);
            } else if(bookingTypeManager.isOnlineBooking(booking)){
                processBooking(seatBookings, onlineBooking, booking);
            } else {
                //add dues based on agent
                booking.setId(null);
                booking.setHasValidAgent(bookingTypeManager.hasValidAgent(booking));
                if(!booking.isDue()) {
                    serviceReport.setNetCashIncome(serviceReport.getNetCashIncome()+ booking.getNetAmt());
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
                if(booking.getSeats() != null) {
                    String[] seats = booking.getSeats().split(",");
                    booking.setSeatsCount(seats.length);
                    serviceForm.setSeatsCount(serviceForm.getSeatsCount() + seats.length);
                }
                cashIncome += booking.getNetAmt();
            }
        }

        if(redbusBooking.getSeatsCount() > 0){
            redbusBooking.setNetAmt(roundUp(redbusBooking.getNetAmt()));
            serviceForm.getBookings().add(redbusBooking);
            serviceForm.setSeatsCount(serviceForm.getSeatsCount() + redbusBooking.getSeatsCount());
        }
        if(onlineBooking.getSeatsCount() > 0) {
            onlineBooking.setNetAmt(roundUp(onlineBooking.getNetAmt()));
            serviceForm.getBookings().add(onlineBooking);
            serviceForm.setSeatsCount(serviceForm.getSeatsCount() + onlineBooking.getSeatsCount());
        }
        for(Payment expense : serviceReport.getExpenses()) {
            serviceReport.setNetCashIncome(serviceReport.getNetCashIncome()- expense.getAmount());
        }
        serviceForm.setNetRedbusIncome(serviceReport.getNetRedbusIncome());
        serviceForm.setNetOnlineIncome(serviceReport.getNetOnlineIncome());
        serviceForm.setNetCashIncome(serviceReport.getNetCashIncome());
        //branchOfficeMongoDAO.updateCashBalance(currentUser.getBranchOfficeId(), serviceForm.getNetCashIncome());
        serviceForm.setNetIncome(serviceReport.getNetRedbusIncome() + serviceReport.getNetOnlineIncome() + cashIncome);
        serviceForm.setSource(serviceReport.getSource());
        serviceForm.setDestination(serviceReport.getDestination());
        serviceForm.setBusType(serviceReport.getBusType());
        serviceForm.setVehicleRegNumber(serviceReport.getVehicleRegNumber());
        serviceForm.setJDate(serviceReport.getJourneyDate());
        serviceForm.setConductorInfo(serviceReport.getConductorInfo());
        serviceForm.setNotes(serviceReport.getNotes());
        ServiceForm savedForm =  serviceFormDAO.save(serviceForm);
        paymentManager.createPayment(serviceForm);
        serviceForm.getBookings().stream().forEach(booking -> {booking.setFormId(savedForm.getId());});
        serviceReport.getExpenses().stream().forEach(expense -> {expense.setFormId(savedForm.getId());});
        paymentDAO.save(serviceReport.getExpenses());
        bookingDAO.save(serviceForm.getBookings());
        serviceReport.getAttributes().put(ServiceReport.SUBMITTED_ID, savedForm.getId());
        serviceReportDAO.save(serviceReport);
        return serviceForm;
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
        ServiceForm report = serviceFormDAO.findOne(id);
        Iterable<Booking> bookings = bookingDAO.findByFormId(report.getId());
        //round up the digits
        report.setNetRedbusIncome(roundUp(report.getNetRedbusIncome()));
        report.setNetOnlineIncome(roundUp(report.getNetOnlineIncome()));
        report.setNetCashIncome(roundUp(report.getNetCashIncome()));
        report.setNetIncome(roundUp(report.getNetIncome()));
        report.setExpenses(IteratorUtils.toList(paymentDAO.findByFormId(id).iterator()));
        report.setBookings(IteratorUtils.toList(bookings.iterator()));
        return report;
    }
}
