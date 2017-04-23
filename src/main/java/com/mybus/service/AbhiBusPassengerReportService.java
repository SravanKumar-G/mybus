package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Booking;
import com.mybus.model.ReportDownloadStatus;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
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


    public List<ServiceReport> getServicesByDate(String date) throws Exception{
        logger.info("downloading reports for date:" + date);
        init();
        Map<String, ServiceReport> serviceBookings = new HashMap<>();
        List<ServiceReport> serviceReports = new ArrayList<>();
        HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
        inputParam.put("jdate", date);
        Vector params = new Vector();
        params.add(inputParam);
        HashMap busLists = (HashMap) xmlRpcClient.execute("index.serviceslist", params);
        Object busList[] = null;
        Iterator it = busLists.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            busList = (Object[]) pair.getValue();
            logger.info("services size:"+busList.length);
        }
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
            
           serviceReports.add(serviceReport); 
        }
        
       return serviceReports;
    }
    
    
    
    public List<ServiceReport> getServiceDetailsByNumberAndDate(String serviceNum, String date) throws Exception{
        logger.info("downloading service details for date and service numnbers:" + date);
        init();
        List<String> serviceComboNumbers = serviceComboManager.getServiceComboNumbers();
        Map<String, ServiceReport> serviceBookings = new HashMap<>();
        List<ServiceReport> serviceReports = new ArrayList<>();
        HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
        inputParam.put("jdate", date);
        inputParam.put("serviceids", serviceNum);
        Vector params = new Vector();
        params.add(inputParam);
        Object busList[] = (Object[]) xmlRpcClient.execute("index.passengerreport", params);
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
            Set<Booking> bookings = new HashSet<>();
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
                    serviceReport.getBookings().add(booking);
                }catch (Exception e) {
                    throw new BadRequestException("Failed downloading reports");
                }
            }
            if(serviceComboNumbers.contains(serviceReport.getServiceNumber())) {
                serviceBookings.put(serviceReport.getServiceNumber(), serviceReport);
            } else {
                serviceReports.add(serviceReport);
            }   
        } 
       return serviceReports;
    }

    /**
     * Module that downloads the passenger report creates service reports and bookings
     * @param date
     * @return
     * @throws Exception
     */
    public ServiceReportStatus downloadReport(String date) throws Exception{
        logger.info("downloading reports for date:" + date);
        ServiceReportStatus serviceReportStatus = serviceReportStatusDAO.findByReportDate(ServiceConstants.df.parse(date));
        if(serviceReportStatus != null) {
            logger.info("The reports are being downloaded for " + date);
            return null;
        }
        serviceReportStatus = serviceReportStatusDAO.save
                (new ServiceReportStatus(ServiceConstants.df.parse(date), ReportDownloadStatus.DOWNLOADING));
        init();
        List<String> serviceComboNumbers = serviceComboManager.getServiceComboNumbers();
        Map<String, ServiceReport> serviceBookings = new HashMap<>();
        Collection<ServiceReport> serviceReports = new ArrayList<>();
        HashMap<Object, Object> inputParam = new HashMap<Object, Object>();
        inputParam.put("jdate", date);
        Vector params = new Vector();
        params.add(inputParam);
        Object busList[] = (Object[]) xmlRpcClient.execute("index.passengerreport", params);
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
            Set<Booking> bookings = new HashSet<>();
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
                    serviceReport.getBookings().add(booking);
                }catch (Exception e) {
                    throw new BadRequestException("Failed downloading reports");
                }
            }
            if(serviceComboNumbers.contains(serviceReport.getServiceNumber())) {
                serviceBookings.put(serviceReport.getServiceNumber(), serviceReport);
            } else {
                serviceReports.add(serviceReport);
            }
        }
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
            final ServiceReport savedReport = serviceReportDAO.save(serviceReport);
            bookings.stream().forEach(booking -> {
                booking.setServiceId(savedReport.getId());
            });
            bookingDAO.save(bookings);
        }
        serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
        return serviceReportStatusDAO.save(serviceReportStatus);
    }
    public static void main(String args[]) {
        try {
            new AbhiBusPassengerReportService().getServiceDetailsByNumberAndDate("SKT-888BA", "2017-04-15");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}