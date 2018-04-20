package com.mybus.service;


import com.mashape.unirest.http.Unirest;
import com.mybus.SystemProperties;
import com.mybus.model.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.net.URL;

@Service
@SessionScope
public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    public static XmlRpcClient xmlRpcClient;

    public void initAbhibus(OperatorAccount operatorAccount) {
        if(operatorAccount.getApiURL() == null){
            new RuntimeException("Invalid API URL");
        }
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(operatorAccount.getApiURL()));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
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
}
