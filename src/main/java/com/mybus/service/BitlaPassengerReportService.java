package com.mybus.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class BitlaPassengerReportService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(BitlaPassengerReportService.class);

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private ServiceListingDAO serviceListingDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private BookingTypeManager bookingTypeManager;
    /**
     * Module that downloads the passenger report creates service reports and bookings
     * @param date
     * @return
     * @throws Exception
     */
    public ServiceReportStatus downloadReports(String date){
        logger.info("downloading reports for date:" + date);
        ServiceReportStatus serviceReportStatus = null;
        try{
            serviceReportStatus = serviceReportStatusDAO.findByReportDateBetweenAndOperatorId(
                    ServiceUtils.parseDate(date, false),
                    ServiceUtils.parseDate(date, true), sessionManager.getOperatorId());
            if(serviceReportStatus != null) {
                logger.info("The reports are being downloaded for " + date);
                return null;
            }
            serviceReportStatus = serviceReportStatusDAO.save
                    (new ServiceReportStatus(ServiceConstants.parseDate(date), ReportDownloadStatus.DOWNLOADING));
            serviceReportStatus.setOperatorId(sessionManager.getOperatorId());
            String key = loginBitlaBus();
            //GET http://jagan.jagantravels.com/api/get_passenger_details/2018-02-16.json?api_key=84FEZH5KE3KWAKIQDIZ6R7Q3KWZZT7RW
            String url = String.format("http://jagan.jagantravels.com/api/get_passenger_details/%s.json?api_key=%s", date, key);
            HttpResponse<JsonNode> response = Unirest.get(url).asJson();
            JSONArray serviceListings = response.getBody().getArray();
            for(Object serviceListing : serviceListings){
                JSONObject serviceInfo = (JSONObject) serviceListing;
                ServiceReport serviceReport = new ServiceReport();
                if(serviceInfo.has("travel_date")){
                    serviceReport.setJDate(serviceInfo.get("travel_date").toString());
                }
                if(serviceInfo.has("route_num")){
                    serviceReport.setServiceNumber(serviceInfo.get("route_num").toString());
                }
                if(serviceInfo.has("route_id")){
                    serviceReport.setServiceId(serviceInfo.get("route_id").toString());
                }
                //reload the service report if found
                ServiceReport savedReport = serviceReportDAO.findByJDateAndServiceIdAndOperatorId(serviceReport.getJDate(), serviceReport.getServiceId(), sessionManager.getOperatorId());
                if(savedReport != null) {
                    serviceReport = savedReport;
                }
                if(serviceInfo.has("origin")){
                    serviceReport.setSource(serviceInfo.getString("origin"));
                }
                if(serviceInfo.has("destination")){
                    serviceReport.setDestination(serviceInfo.getString("destination"));
                }
                if(serviceInfo.has("travel_date")){
                    serviceReport.setJourneyDate(ServiceConstants.parseDate(serviceInfo.getString("travel_date")));
                }
                if(serviceInfo.has("bus_type")){
                    serviceReport.setBusType(serviceInfo.get("bus_type").toString());
                }
                if(serviceInfo.has("coach_num")){
                    serviceReport.setVehicleRegNumber(serviceInfo.get("coach_num").toString());
                }
                serviceReport.setJourneyDate(ServiceConstants.parseDate(date));
                serviceReport.setOperatorId(sessionManager.getOperatorId());
                serviceReport = serviceReportDAO.save(serviceReport);
                serviceListingDAO.save(new ServiceListing(serviceReport, sessionManager));
                createServiceReports(date, serviceReport, serviceInfo.getJSONArray("passenger_details"));
                serviceReportDAO.save(serviceReport); //save it again for saving the cashincome
            }
            serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
        }catch (Exception e) {
            serviceReportStatusDAO.delete(serviceReportStatus);
            e.printStackTrace();
            throw new BadRequestException("Failed downloading the reports " + e);
        }
        logger.info("Done: downloading reports for date:" + date);
        return serviceReportStatusDAO.save(serviceReportStatus);
    }

    private String loginBitlaBus() throws UnirestException {
        HttpResponse<JsonNode> postResponse = Unirest.post("http://jagan.jagantravels.com/api/login.json").field("login","jagan.srini")
                .field("password","1234qweR").asJson();
        String key = postResponse.getBody().getObject().getString("key");
        if(key == null){
            throw new BadRequestException("Login failed");
        }
        return key;
    }


    private  void createServiceReports(String date,
                                      ServiceReport serviceReport, JSONArray passengers) throws ParseException {
        for (Object info: passengers) {
            JSONObject passengerInfo = (JSONObject) info;
            Booking booking = new Booking();
            try {
                booking.setTicketNo(passengerInfo.getString("pnr_number"));
                Booking savedBooking = bookingDAO.findByTicketNoAndOperatorId(booking.getTicketNo(), sessionManager.getOperatorId());
                if(savedBooking != null) {
                    booking = savedBooking;
                }
                booking.setServiceReportId(serviceReport.getId());
                booking.setServiceName(serviceReport.getServiceName());
                booking.setServiceNumber(serviceReport.getServiceNumber());
                booking.setJDate(date);
                booking.setJourneyDate(ServiceConstants.parseDate(booking.getJDate()));
                booking.setPhoneNo(passengerInfo.getString("mobile"));
                booking.setSeats(passengerInfo.getString("seat_number").replace(",", ", "));
                booking.setName(passengerInfo.getString("name"));
                booking.setSource(serviceReport.getSource());
                booking.setDestination(serviceReport.getDestination());
                booking.setBookedBy(passengerInfo.getString("booked_by"));
                booking.setBookingType(String.valueOf(passengerInfo.getInt("booking_type_id")));
                //booking.setBookedDate(passengerInfo.get("BookedDate").toString());
                booking.setBasicAmount(Double.valueOf(String.valueOf(passengerInfo.get("basic_amount"))));
                booking.setServiceTax(Double.parseDouble(String.valueOf(passengerInfo.get("service_tax_amount"))));
                booking.setCommission(Double.parseDouble(String.valueOf(passengerInfo.get("commission_amount"))));
                booking.setNetAmt(Double.parseDouble(passengerInfo.get("net_amount").toString()));
                booking.setEmailID(passengerInfo.get("email").toString());
                booking.setBoardingPoint(passengerInfo.getString("boarding_at"));
                booking.setLandmark(passengerInfo.getString("boarding_address"));
                booking.setBoardingTime(passengerInfo.getString("bording_date_time"));
                booking.setOriginalCost(booking.getNetAmt());
                booking.setOperatorId(sessionManager.getOperatorId());
                calculateServiceReportIncome(serviceReport, booking);
                bookingDAO.save(booking);
            }catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Failed downloading reports");
            }
        }
    }
    private void calculateServiceReportIncome(ServiceReport serviceReport, Booking booking) {
        if(bookingTypeManager.isRedbusBooking(booking, BookingTypeManager.BITLA_BUS)){
            serviceReport.setNetRedbusIncome(serviceReport.getNetRedbusIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.REDBUS);
            booking.setHasValidAgent(true);
        } else if(bookingTypeManager.isOnlineBooking(booking, BookingTypeManager.BITLA_BUS)) {
            serviceReport.setNetOnlineIncome(serviceReport.getNetOnlineIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.ONLINE);
            booking.setHasValidAgent(true);
        } else {
            Agent bookingAgent = bookingTypeManager.getBookingAgent(booking);
            booking.setHasValidAgent(bookingTypeManager.hasValidAgent(booking));
            serviceReport.setInvalid(bookingAgent == null);
            serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.CASH);
        }
    }
	private double roundUp(double value) {
        return (double) Math.round(value * 100) / 100;
    }


    public static void main(String args[]) {
        try {
            new BitlaPassengerReportService().downloadReports("2016-02-13");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    protected void calculateServiceReportIncome(ServiceReport serviceReport, Booking booking) {
        if(bookingTypeManager.isRedbusBooking(booking)){
            serviceReport.setNetRedbusIncome(serviceReport.getNetRedbusIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.REDBUS);
            booking.setHasValidAgent(true);
        } else if(bookingTypeManager.isOnlineBooking(booking)) {
            serviceReport.setNetOnlineIncome(serviceReport.getNetOnlineIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.ONLINE);
            booking.setHasValidAgent(true);
        } else {
            Agent bookingAgent = bookingTypeManager.getBookingAgent(booking);
            booking.setHasValidAgent(bookingTypeManager.hasValidAgent(booking));
            serviceReport.setInvalid(bookingAgent == null);
            adjustAgentBookingCommission(booking, bookingAgent);
            serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.CASH);
        }
    }

*/
}