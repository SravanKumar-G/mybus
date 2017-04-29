package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.text.ParseException;
import java.util.*;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class AbhiBusPassengerReportService {
    public static XmlRpcClient xmlRpcClient;
    private static final Logger logger = LoggerFactory.getLogger(AbhiBusPassengerReportService.class);

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private ServiceComboManager serviceComboManager;


    @Autowired
    private BookingTypeManager bookingTypeManager;

    @Autowired
    public void init() {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(ServiceConstants.ABHI_BUS_URL));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            serviceReportStatus = serviceReportStatusDAO.findByReportDate(ServiceConstants.df.parse(date));
            if(serviceReportStatus != null) {
                logger.info("The reports are being downloaded for " + date);
                return null;
            }
            serviceReportStatus = serviceReportStatusDAO.save
                    (new ServiceReportStatus(ServiceConstants.df.parse(date), ReportDownloadStatus.DOWNLOADING));
            init();
            List<String> serviceComboNumbers = serviceComboManager.getServiceComboNumbers();
            Map<String, ServiceReport> serviceComboBookings = new HashMap<>();
            Collection<ServiceReport> serviceReports = new ArrayList<>();
            HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
            inputParam.put("jdate", date);
            Vector params = new Vector();
            params.add(inputParam);
            Object busList[] = (Object[]) xmlRpcClient.execute("index.passengerreport", params);
            processDownloadedReports(date, serviceComboNumbers, serviceComboBookings, serviceReports, busList);
            processServiceCombos(serviceComboBookings, serviceReports);
            serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
        }catch (Exception e) {
            serviceReportStatusDAO.delete(serviceReportStatus);
            e.printStackTrace();
            throw new BadRequestException("Failed downloading the reports");
        }
        return serviceReportStatusDAO.save(serviceReportStatus);
    }

    private void processDownloadedReports(String date, List<String> serviceComboNumbers,
                                          Map<String, ServiceReport> serviceComboBookings,
                                          Collection<ServiceReport> serviceReports,
                                          Object[] busList) throws ParseException {
        for (Object busServ: busList) {
            Map busService = (HashMap) busServ;
            ServiceReport serviceReport = new ServiceReport();
            serviceReport.setJourneyDate(ServiceConstants.df.parse(date));
            if(busService.containsKey("ServiceId")){
                serviceReport.setServiceNumber(busService.get("ServiceId").toString());
            }
            if(busService.containsKey("ServiceName")){
                serviceReport.setServiceName(busService.get("ServiceName").toString());
            }
            if(busService.containsKey("ServiceNumber")){
                serviceReport.setServiceNumber(busService.get("ServiceNumber").toString());
            }
            if(busService.containsKey("BusType")){
                serviceReport.setBusType(busService.get("BusType").toString());
            }
            if(busService.containsKey("Service_Source")){
                serviceReport.setSource(busService.get("Service_Source").toString());
            }
            if(busService.containsKey("Service_Destination")){
                serviceReport.setDestination(busService.get("Service_Destination").toString());
            }
            if(busService.containsKey("Vehicle_No")){
                serviceReport.setVehicleRegNumber(busService.get("Vehicle_No").toString());
            }

            if(busService.containsKey("Conductor_Name") && busService.get("Conductor_Name") != null){
                String conductorInfo = busService.get("Conductor_Name").toString();
                if(busService.containsKey("Conductor_Phone")
                        && busService.get("Conductor_Phone") != null
                        && busService.get("Conductor_Phone").toString().trim().length() > 0) {
                    conductorInfo += (" " + Long.parseLong(busService.get("Conductor_Phone").toString()));
                }
                serviceReport.setConductorInfo(conductorInfo);
            }
            Object[] passengerInfos = (Object[]) busService.get("PassengerInfo");
            for (Object info: passengerInfos) {
                Map passengerInfo = (HashMap) info;
                Booking booking = new Booking();
                try {
                    //booking.setServiceId(serviceReport.getId());
                    booking.setServiceName(serviceReport.getServiceName());
                    booking.setServiceNumber(serviceReport.getServiceNumber());
                    booking.setTicketNo(passengerInfo.get("TicketNo").toString());
                    booking.setJDate(passengerInfo.get("JourneyDate").toString());
                    booking.setJourneyDate(ServiceConstants.df.parse(booking.getJDate()));
                    //passenger.put("StartTime", passengerInfo.get("StartTime"));
                    booking.setPhoneNo(passengerInfo.get("Mobile").toString());
                    booking.setSeats(passengerInfo.get("Seats").toString());
                    booking.setName(passengerInfo.get("PassengerName").toString());
                    booking.setSource(passengerInfo.get("Source").toString());
                    booking.setDestination(passengerInfo.get("Destination").toString());
                    booking.setBookedBy(passengerInfo.get("BookedBy").toString());
                    booking.setBookedDate(passengerInfo.get("BookedDate").toString());
                    booking.setBasicAmount(Double.valueOf(String.valueOf(passengerInfo.get("BasicAmt"))));
                    booking.setServiceTax(Double.parseDouble(String.valueOf(passengerInfo.get("ServiceTaxAmt"))));
                    booking.setCommission(Double.parseDouble(String.valueOf(passengerInfo.get("Commission"))));
                    booking.setBoardingPoint(passengerInfo.get("BoardingPoint").toString());
                    booking.setLandmark(passengerInfo.get("Landmark").toString());
                    booking.setBoardingTime(passengerInfo.get("BoardingTime").toString());
                    booking.setOrderId(passengerInfo.get("OrderId").toString());
                    booking.setNetAmt(Double.parseDouble(passengerInfo.get("NetAmt").toString()));
                    processBookingForPayment(serviceReport, booking);
                    serviceReport.getBookings().add(booking);

                }catch (Exception e) {
                    throw new BadRequestException("Failed downloading reports");
                }
            }
            if(serviceComboNumbers.contains(serviceReport.getServiceNumber())) {
                serviceComboBookings.put(serviceReport.getServiceNumber(), serviceReport);
            } else {
                serviceReports.add(serviceReport);
            }
        }
    }
    private double roundUp(double value) {
        return (double) Math.round(value * 100) / 100;
    }
    private void processServiceCombos(Map<String, ServiceReport> serviceBookings, Collection<ServiceReport> serviceReports) {
        //get the combo mappings and merge them
        Map<String, String[]> serviceComboMappings = serviceComboManager.getServiceComboMappings();
        for(Map.Entry<String, String[]> entry :serviceComboMappings.entrySet()) {
            ServiceReport serviceReport = serviceBookings.get(entry.getKey());
            if(serviceReport != null) {
                for(String serviceNumber: entry.getValue()) {
                    ServiceReport comboReport = serviceBookings.get(serviceNumber);
                    if(comboReport != null) {
                        serviceReport.setBusType(serviceReport.getBusType() + " " + comboReport.getBusType());
                        serviceReport.getBookings().addAll(comboReport.getBookings());
                    }
                }
                serviceReports.add(serviceReport);
            }
        }
        for(ServiceReport serviceReport : serviceReports) {
            Collection<Booking> bookings = serviceReport.getBookings();
            serviceReport.setBookings(null);
            serviceReport.setNetRedbusIncome(roundUp(serviceReport.getNetRedbusIncome()));
            serviceReport.setNetOnlineIncome(roundUp(serviceReport.getNetOnlineIncome()));
            serviceReport.setNetCashIncome(roundUp(serviceReport.getNetCashIncome()));
            serviceReport.setNetIncome(roundUp(serviceReport.getNetCashIncome() +
                    serviceReport.getNetOnlineIncome() + serviceReport.getNetRedbusIncome()));
            final ServiceReport savedReport = serviceReportDAO.save(serviceReport);
            bookings.stream().forEach(booking -> {
                booking.setServiceId(savedReport.getId());
            });
            bookingDAO.save(bookings);
        }
    }

    private void processBookingForPayment(ServiceReport serviceReport, Booking booking) {
        if(bookingTypeManager.isRedbusBooking(booking)){
            serviceReport.setNetRedbusIncome(serviceReport.getNetRedbusIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.REDBUS);
        } else if(bookingTypeManager.isOnlineBooking(booking)) {
            serviceReport.setNetOnlineIncome(serviceReport.getNetOnlineIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.ONLINE);
        } else {
            Agent bookingAgent = bookingTypeManager.getBookingAgent(booking);
            booking.setHasValidAgent(bookingAgent != null);
            serviceReport.setInvalid(bookingAgent == null);
            adjustAgentBookingCommission(booking, bookingAgent);
            serviceReport.setNetCashIncome(serviceReport.getNetCashIncome() + booking.getNetAmt());
            booking.setPaymentType(BookingType.CASH);
        }
    }
    private void adjustAgentBookingCommission(Booking booking, Agent bookingAgent) {
        if(bookingAgent != null) {
            if(bookingAgent.getCommission() > 0) {
                double netShare = (double)(100 - bookingAgent.getCommission()) / 100;
                booking.setNetAmt(booking.getNetAmt() * netShare);
            }
        }
    }
    public static void main(String args[]) {
        try {
            new AbhiBusPassengerReportService().downloadReports("2016-02-13");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}