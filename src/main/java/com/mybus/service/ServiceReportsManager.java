package com.mybus.service;

import com.mybus.dao.*;
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


import java.awt.print.Book;
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
    private ExpenseDAO expenseDAO;

    @Autowired
    private ServiceFormDAO serviceFormDAO;

    public JSONObject getDownloadStatus(String date) {
        JSONObject response = new JSONObject();
        Iterator<ServiceReportStatus> statusIterator = serviceReportStatusDAO
                .findByReportDate(date).iterator();
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

    public Iterable<ServiceReport> getReports(String date) {
        JSONObject query = new JSONObject();
        query.put("jDate", date);
        return serviceReportMongoDAO.findReports(query, null);
    }

    public ServiceReport getReport(String id) {
        ServiceReport report = serviceReportDAO.findOne(id);
        Iterable<Booking> bookings = bookingDAO.findByServiceId(report.getId());
        for(Booking booking:bookings) {
            if(BookingTypeManager.isRedbusBooking(booking)){
                report.setNetRedbusIncome(report.getNetRedbusIncome() + booking.getNetAmt());
                booking.setPaymentType(PaymentType.REDBUS);
            } else if(BookingTypeManager.isOnlineBooking(booking)) {
                report.setNetOnlineIncome(report.getNetOnlineIncome() + booking.getNetAmt());
                booking.setPaymentType(PaymentType.ONLINE);
            } else {
                report.setNetCashIncome(report.getNetCashIncome() + booking.getNetAmt());
                booking.setPaymentType(PaymentType.CASH);
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
        serviceForm.setServiceReportId(serviceReport.getId());
        Map<String,List<String>> seatBookings = new HashMap<>();
        Booking redbusBooking = new Booking();
        redbusBooking.setBookedBy(BookingTypeManager.REDBUS_CHANNEL);
        Booking onnlineBooking = new Booking();
        onnlineBooking.setBookedBy(BookingTypeManager.ONLINE_CHANNEL);
        double cashIncome = 0;
        for(Booking booking: serviceReport.getBookings()) {
            if(BookingTypeManager.isRedbusBooking(booking)) {
                if(seatBookings.get(BookingTypeManager.REDBUS_CHANNEL) == null) {
                    seatBookings.put(BookingTypeManager.REDBUS_CHANNEL, new ArrayList<>());
                }
                String[] seats = booking.getSeats().split(",");
                if(redbusBooking.getSeats() == null) {
                    redbusBooking.setSeats(booking.getSeats());
                } else {
                    redbusBooking.setSeats(redbusBooking.getSeats() +","+booking.getSeats());
                }
                redbusBooking.setSeatsCount(redbusBooking.getSeatsCount()+seats.length);
                redbusBooking.setNetAmt(redbusBooking.getNetAmt() + booking.getNetAmt());
            }else  if(BookingTypeManager.isOnlineBooking(booking)) {
                String[] seats = booking.getSeats().split(",");
                if(onnlineBooking.getSeats() == null) {
                    onnlineBooking.setSeats(booking.getSeats());
                } else {
                    onnlineBooking.setSeats(onnlineBooking.getSeats() + "," + booking.getSeats());
                }
                onnlineBooking.setSeatsCount(onnlineBooking.getSeatsCount()+seats.length);
                onnlineBooking.setNetAmt(onnlineBooking.getNetAmt() + booking.getNetAmt());
            } else {
                //add dues based on agent
                booking.setId(null);
                booking.setServiceId(null);
                serviceForm.getBookings().add(booking);
                String[] seats = booking.getSeats().split(",");
                booking.setSeatsCount(seats.length);
                serviceForm.setSeatsCount(serviceForm.getSeatsCount() + seats.length);
                cashIncome += booking.getNetAmt();
            }
        }

        redbusBooking.setNetAmt(roundUp(redbusBooking.getNetAmt()));
        onnlineBooking.setNetAmt(roundUp(onnlineBooking.getNetAmt()));
        serviceForm.getBookings().add(redbusBooking);
        serviceForm.setSeatsCount(serviceForm.getSeatsCount() + redbusBooking.getSeatsCount());
        serviceForm.getBookings().add(onnlineBooking);
        serviceForm.setSeatsCount(serviceForm.getSeatsCount() + onnlineBooking.getSeatsCount());
        serviceForm.setExpenses(serviceReport.getExpenses());
        serviceForm.setNetRedbusIncome(serviceReport.getNetRedbusIncome());
        serviceForm.setNetOnlineIncome(serviceReport.getNetOnlineIncome());
        serviceForm.setNetCashIncome(serviceReport.getNetCashIncome());
        serviceForm.setNetIncome(serviceReport.getNetRedbusIncome() + serviceReport.getNetOnlineIncome() + cashIncome);
        serviceForm.setSource(serviceReport.getSource());
        serviceForm.setDestination(serviceReport.getDestination());
        serviceForm.setBusType(serviceReport.getBusType());
        serviceForm.setVehicleRegNumber(serviceReport.getVehicleRegNumber());
        serviceForm.setJDate(serviceReport.getJDate());
        serviceForm.setConductorInfo(serviceReport.getConductorInfo());
        serviceForm.setNotes(serviceReport.getNotes());
        ServiceForm savedForm =  serviceFormDAO.save(serviceForm);
        for(Booking booking:serviceForm.getBookings()) {
            booking.setFormId(savedForm.getId());
        }
        for(Expense expense: serviceReport.getExpenses()) {
            expense.setServiceId(serviceForm.getId());
        }
        expenseDAO.save(serviceReport.getExpenses());
        bookingDAO.save(serviceForm.getBookings());
        serviceReport.getAttributes().put(ServiceReport.SUBMITTED_ID, savedForm.getId());
        serviceReportDAO.save(serviceReport);
        return serviceForm;
    }

    public ServiceForm getForm(String id) {
        ServiceForm report = serviceFormDAO.findOne(id);
        Iterable<Booking> bookings = bookingDAO.findByFormId(report.getId());
        //round up the digits
        report.setNetRedbusIncome(roundUp(report.getNetRedbusIncome()));
        report.setNetOnlineIncome(roundUp(report.getNetOnlineIncome()));
        report.setNetCashIncome(roundUp(report.getNetCashIncome()));
        report.setNetIncome(roundUp(report.getNetIncome()));
        report.setExpenses(IteratorUtils.toList(expenseDAO.findByServiceId(id).iterator()));
        report.setBookings(IteratorUtils.toList(bookings.iterator()));
        return report;
    }
}
