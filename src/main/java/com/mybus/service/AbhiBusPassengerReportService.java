package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
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
    private static final String ABHI_BUS_URL = "http://api.abhibus.com/abhibusoperators/srikrishna/server.php?SecurityKey=SRI*FDEU!@@%ANHSIRK";
    public static XmlRpcClient xmlRpcClient;
    private static final Logger logger = LoggerFactory.getLogger(AbhiBusPassengerReportService.class);

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    public void init() {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(ABHI_BUS_URL));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServiceReportStatus downloadReport(String date) throws Exception{
        logger.info("downloading reports for date:" + date);
        ServiceReportStatus serviceReportStatus = new ServiceReportStatus();
        serviceReportStatus.setReportDate(ServiceConstants.df.parse(date));
        serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADING);
        serviceReportStatusDAO.save(serviceReportStatus);
        init();

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
            serviceReport = serviceReportDAO.save(serviceReport);
            Object[] passengerInfos = (Object[]) busService.get("PassengerInfo");
            Set<Booking> bookings = new HashSet<>();
            for (Object info: passengerInfos) {
                Map passengerInfo = (HashMap) info;
                Booking booking = new Booking();
                try {
                    booking.setServiceId(serviceReport.getId());
                    booking.setTicketNo(passengerInfo.get("TicketNo").toString());
                    booking.setJDate(passengerInfo.get("JourneyDate").toString());
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
                    //bookings.add(booking);
                    booking = bookingDAO.save(booking);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //serviceReport.setBookings(bookings);
            serviceReports.add(serviceReport);
        }
        serviceReportDAO.save(serviceReports);
        serviceReportStatus.setStatus(ReportDownloadStatus.DOWNLOADED);
        return serviceReportStatusDAO.save(serviceReportStatus);
    }
    public static void main(String args[]) {
        try {
            new AbhiBusPassengerReportService().downloadReport("2016-02-13");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}