package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.dao.SubmittedServiceReportDAO;
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
    private SubmittedServiceReportDAO submittedServiceReportDAO;

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
        report.setNetRedbusIncome((double) Math.round(report.getNetRedbusIncome() * 100) / 100);
        report.setNetOnlineIncome((double) Math.round(report.getNetOnlineIncome() * 100) / 100);
        report.setNetCashIncome((double) Math.round(report.getNetCashIncome() * 100) / 100);

        report.setNetIncome(report.getNetCashIncome()+report.getNetOnlineIncome()+report.getNetRedbusIncome());
        report.setBookings(IteratorUtils.toList(bookings.iterator()));
        return report;
    }
    public SubmittedServiceReport submitReport(ServiceReport serviceReport) {
        logger.info("submitting the report");
        SubmittedServiceReport submittedServiceReport = new SubmittedServiceReport();
        submittedServiceReport.setServiceReportId(serviceReport.getId());
        Map<String,List<String>> seatBookings = new HashMap<>();
        Booking redbusBooking = new Booking();
        Booking onnlineBooking = new Booking();
        double rebBusIncome =0, onlineIncome =0;
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
                submittedServiceReport.getBookings().add(booking);
                String[] seats = booking.getSeats().split(",");
                submittedServiceReport.setSeatsCount(submittedServiceReport.getSeatsCount() + seats.length);
            }
        }
        submittedServiceReport.getBookings().add(redbusBooking);
        submittedServiceReport.setSeatsCount(submittedServiceReport.getSeatsCount() + redbusBooking.getSeatsCount());
        submittedServiceReport.getBookings().add(onnlineBooking);
        submittedServiceReport.setSeatsCount(submittedServiceReport.getSeatsCount() + onnlineBooking.getSeatsCount());
        submittedServiceReport.setExpenses(serviceReport.getExpenses());
        submittedServiceReport.setNetCashIncome(serviceReport.getNetCashIncome());
        submittedServiceReport.setSource(serviceReport.getSource());
        submittedServiceReport.setDestination(serviceReport.getDestination());
        submittedServiceReport.setBusType(serviceReport.getBusType());
        submittedServiceReport.setVehicleRegNumber(serviceReport.getVehicleRegNumber());
        submittedServiceReport.setJDate(serviceReport.getJDate());
        submittedServiceReport.setConductorInfo(serviceReport.getConductorInfo());
        submittedServiceReport =  submittedServiceReportDAO.save(submittedServiceReport);
        serviceReport.getAttributes().put(ServiceReport.SUBMITTED_ID, submittedServiceReport.getId());
        serviceReportDAO.save(serviceReport);
        return submittedServiceReport;
    }
}
