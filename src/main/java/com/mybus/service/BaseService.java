package com.mybus.service;

import com.mybus.SystemProperties;
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

    public static XmlRpcClient xmlRpcClient;

    @Autowired
    private SystemProperties systemProperties;

    public void init() {
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

}
