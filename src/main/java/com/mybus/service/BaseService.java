package com.mybus.service;

import com.mybus.SystemProperties;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class BaseService {

    public static XmlRpcClient xmlRpcClient;

    @Autowired
    private SystemProperties systemProperties;

    public void init() {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(systemProperties.getProperty(SystemProperties.SysProps.ABHIBUS_API_URL)));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
