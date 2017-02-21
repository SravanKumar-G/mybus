package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.model.Booking;
import com.mybus.model.PaymentType;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Iterator;

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
            } else if(BookingTypeManager.isAbhibusBooking(booking)) {
                report.setNetOnlineIncome(report.getNetOnlineIncome() + booking.getNetAmt());
                booking.setPaymentType(PaymentType.ONLINE);
            } else {
                report.setNetCashIncome(report.getNetCashIncome() + booking.getNetAmt());
                booking.setPaymentType(PaymentType.CASH);
            }
        }
        report.setNetIncome(report.getNetCashIncome()+report.getNetOnlineIncome()+report.getNetRedbusIncome());
        report.setBookings(IteratorUtils.toList(bookings.iterator()));
        return report;
    }
}
