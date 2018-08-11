package com.mybus.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mybus.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSManager {

    @Autowired
    private SystemProperties systemProperties;
    /**
    public void sendSMS(String number, String message) {
        String senderName = "EASYGAADI";
        String apiKey = systemProperties.getProperty(SystemProperties.SysProps.SMS_GATEWAY_API_KEY);
        String url = String.format("http://api.msg91.com/api/sendhttp.php?sender=%s&route")
        props.getIntegerProperty(SystemProperties.SysProps.MAX_UPLOAD_SIZE_BYTES.getPropertyName(), MAX_UPLOAD_SIZE_DEFAULT);
        HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=MSGIND&route=4&mobiles=&authkey=&encrypt=&country=0&message=Hello!%20This%20is%20a%20test%20message&flash=&unicode=&schtime=&afterminutes=&response=&campaign=")
                .asString();
    }*/

}
