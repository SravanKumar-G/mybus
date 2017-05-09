package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceComboDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.dao.impl.ServiceComboMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.json.simple.JSONObject;
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
    private ServiceComboMongoDAO serviceComboMongoDAO;

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
     * Find active services for a given date
     * @param date
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> getActiveServicesByDate(String date) throws Exception{
        logger.info("downloading reports for date:" + date);
        init();
        Map<String, Map<String, String>> serviceReportsMap = new HashMap<>();
        HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
        inputParam.put("jdate", date);
        Vector params = new Vector();
        params.add(inputParam);
        HashMap busLists = (HashMap) xmlRpcClient.execute("index.serviceslist", params);
        logger.info("Bus Leng"+busLists.size());
        Object busList[] = null;
        Iterator it = busLists.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            busList = (Object[]) pair.getValue();
            logger.info("services size:"+busList.length);
            for (Object busServ: busList) {
                Map busService = (HashMap) busServ;
                Map<String, String> serviceInfo = new JSONObject();
                if(busService.containsKey("ServiceId")){
                    serviceInfo.put("serviceId", busService.get("ServiceId").toString());
                }
                if(busService.containsKey("ServiceName")){
                    serviceInfo.put("serviceName", busService.get("ServiceName").toString());
                }
                if(busService.containsKey("ServiceNumber")){
                    serviceInfo.put("serviceNumber", busService.get("ServiceNumber").toString());
                }
                if(busService.containsKey("BusType")){
                    serviceInfo.put("busType", busService.get("BusType").toString());
                }
                if(busService.containsKey("Service_Source")){
                    serviceInfo.put("source", busService.get("Service_Source").toString());
                }
                if(busService.containsKey("Service_Destination")){
                    serviceInfo.put("destination", busService.get("Service_Destination").toString());
                }
                if(busService.containsKey("Vehicle_No")){
                    serviceInfo.put("vehicleRegNumber", busService.get("Vehicle_No").toString());
                }
                serviceReportsMap.put(serviceInfo.get("serviceNumber"), serviceInfo);
            }
        }
        List<Map<String, String>> serviceReports = new ArrayList<>();
        Map<String, String[]> serviceComboMappings = serviceComboManager.getServiceComboMappings();
        for(Map.Entry<String, String[]> serviceCombo: serviceComboMappings.entrySet()){
            Map<String, String> serviceReportInfo = serviceReportsMap.remove(serviceCombo.getKey());
            if(serviceReportInfo != null){
                for(String comboNumber: serviceCombo.getValue()){
                    String currentServiceIds = serviceReportInfo.get("serviceId");
                    Map<String, String> comboService = serviceReportsMap.remove(comboNumber);
                    if(comboService != null) {
                        serviceReportInfo.put("serviceId", currentServiceIds + "," + comboService.get("serviceId"));
                    }
                }
                serviceReports.add(serviceReportInfo);
            }
        }
        serviceReports.addAll(serviceReportsMap.values());
        return serviceReports;
    }

    public List<ServiceReport> getServiceDetailsByNumberAndDate(String serviceNum, String date) throws Exception{
        logger.info("downloading service details for date:" + date);
        init();
        HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
        inputParam.put("jdate", date);
        inputParam.put("serviceids", serviceNum.trim());
        Vector params = new Vector();
        params.add(inputParam);
        Object busList[] = (Object[])  xmlRpcClient.execute("index.passengerreport", params);
        Map<String, ServiceReport> serviceReportsMap = createServiceReports(date, busList);
        List<ServiceReport> serviceReports = mergeServiceCombos(serviceReportsMap);
       return serviceReports;
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
            Map<String, ServiceReport> serviceComboBookings = new HashMap<>();
            HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
            inputParam.put("jdate", date);
            Vector params = new Vector();
            params.add(inputParam);
            Object busList[] = (Object[]) xmlRpcClient.execute("index.passengerreport", params);
            Map<String, ServiceReport> serviceReportsMap = createServiceReports(date, busList);
            mergeServiceCombos(serviceReportsMap);
            serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
        }catch (Exception e) {
            serviceReportStatusDAO.delete(serviceReportStatus);
            e.printStackTrace();
            throw new BadRequestException("Failed downloading the reports");
        }
        return serviceReportStatusDAO.save(serviceReportStatus);
    }

    /**
     * Create a Map<ServiceNumber, ServiceReport> from the downloaded response
     * @param date
     * @param busList
     * @return
     * @throws ParseException
     */
    private  Map<String, ServiceReport> createServiceReports(String date,
                                      Object[] busList) throws ParseException {
        Map<String, ServiceReport> serviceReportsMap = new HashMap<>();
        for (Object busServ: busList) {
            Map busService = (HashMap) busServ;
            ServiceReport serviceReport = new ServiceReport();
            Date journeyDate = ServiceConstants.df.parse(date);
            serviceReport.setJourneyDate(journeyDate);
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
            ServiceReport savedReport = getByJourneyDateAndServiceNumber(serviceReport.getServiceNumber(), journeyDate);
            
            if(savedReport != null) {
                if(savedReport.getStatus() != null) {
                    throw new BadRequestException("The report have been already submitted and can not redownloaded");
                }
                serviceReport = savedReport;
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
                    calculateServiceReportIncome(serviceReport, booking);
                    if(savedReport != null) {
                    	logger.info("found serviceReport");
                        //DO NOT SAVE THE BOOKING IF THIS IS BOOKING FOR ALREADY DOWNLOADED REPORT
                        // AND BOOKING WITH TICKET NUMBER already exists
                    	
                    	logger.info("ticket no :"+ booking.getTicketNo());
                    	logger.info("ticket no :"+ bookingDAO.findByTicketNo(booking.getTicketNo()));
                        if(bookingDAO.findByTicketNo(booking.getTicketNo().trim()) == null) {
                        	logger.info("found booking");
                            serviceReport.getBookings().add(booking);
                        }
                    }else {
                    	logger.info("not found serviceReport");
                        serviceReport.getBookings().add(booking);
                    }
                }catch (Exception e) {
                    throw new BadRequestException("Failed downloading reports");
                }
            }
            serviceReportsMap.put(serviceReport.getServiceNumber(), serviceReport);
        }
        return serviceReportsMap;
    }

    private ServiceReport getByJourneyDateAndServiceNumber(String serviceNumber, Date journeyDate) {
        ServiceCombo serviceCombo = serviceComboMongoDAO.findServiceCombo(serviceNumber);
        return serviceReportDAO.findByJourneyDateAndServiceNumber(journeyDate,
                serviceCombo.getServiceNumber());
    }

	private double roundUp(double value) {
        return (double) Math.round(value * 100) / 100;
    }

    /**
     * Merge the service reports based on combo mappings degfined
     * @param serviceReportMap
     * @return
     */
    private List<ServiceReport> mergeServiceCombos(Map<String, ServiceReport> serviceReportMap) {
        List<ServiceReport> serviceReports = new ArrayList<>();
        Map<String, String[]> serviceComboMappings = serviceComboManager.getServiceComboMappings();

        //get the combo mappings and merge them
        for(Map.Entry<String, String[]> comboEntry :serviceComboMappings.entrySet()) {
            ServiceReport serviceReport = serviceReportMap.remove(comboEntry.getKey());
            if(serviceReport != null) {
                for(String comboNumber: comboEntry.getValue()) {
                    ServiceReport comboReport = serviceReportMap.remove(comboNumber);
                    if(comboReport != null) {
                        serviceReport.setBusType(serviceReport.getBusType() + " " + comboReport.getBusType());
                        serviceReport.getBookings().addAll(comboReport.getBookings());
                        serviceReport.setNetRedbusIncome(roundUp(serviceReport.getNetRedbusIncome() + comboReport.getNetRedbusIncome()));
                        serviceReport.setNetOnlineIncome(roundUp(serviceReport.getNetOnlineIncome() + comboReport.getNetOnlineIncome()));
                        serviceReport.setNetCashIncome(roundUp(serviceReport.getNetCashIncome()+ comboReport.getNetCashIncome()));
                        serviceReport.setNetIncome(roundUp(serviceReport.getNetCashIncome() +
                                serviceReport.getNetOnlineIncome() + serviceReport.getNetRedbusIncome()));
                    }
                    serviceReports.add(serviceReport);
                }
            }
        }
        //add remaining reports to the collection
        serviceReports.addAll(serviceReportMap.values());
        //for every service report set the serviceId in the booking.
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
        return serviceReports;
    }

    private void calculateServiceReportIncome(ServiceReport serviceReport, Booking booking) {
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

    /**
     * Adjust the net income of booking based on agent commission
     * @param booking
     * @param bookingAgent
     */

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