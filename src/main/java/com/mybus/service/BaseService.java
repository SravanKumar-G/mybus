package com.mybus.service;


import com.mashape.unirest.http.Unirest;
import com.mybus.SystemProperties;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BookingType;
import com.mybus.model.ServiceReport;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    private BookingTypeManager bookingTypeManager;

    public static XmlRpcClient xmlRpcClient;

    @Autowired
    private SystemProperties systemProperties;

    public void initAbhibus() {
        try {
            if(xmlRpcClient == null) {
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                String url = systemProperties.getProperty(SystemProperties.SysProps.ABHIBUS_API_URL);
                //logger.info("Using the URL " + url);
                config.setServerURL(new URL(url));
                xmlRpcClient = new XmlRpcClient();
                xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
                xmlRpcClient.setConfig(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String initBitlabus(String hostName) {
        try {
            org.json.JSONObject jsonObject  = Unirest.post("http://jagan.jagantravels.com/api/login.json")
                    .field("login", "jagan.srini")
                    .field("password", "1234qweR")
                    .asJson().getBody().getObject();
            return jsonObject.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
