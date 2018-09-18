package com.mybus.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.SMSNotificationDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.OperatorAccount;
import com.mybus.model.SMSNotification;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import java.util.List;

public class SMSManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SMSNotificationDAO smsNotificationDAO;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SMSManager smsManager;

    @Before
    @After
    public void cleanup(){
        operatorAccountDAO.deleteAll();
        smsNotificationDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount.setSmsSenderName("SRIKRI");
        operatorAccount.setName("test");
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        User user = new User();
        user.setUserName("test");
        user = userDAO.save(user);
        sessionManager.setCurrentUser(user);
    }
    @Test
    @Ignore
    public void testSendSMS() throws UnirestException {
        String number = "+918886665253";
        smsManager.sendSMS(number, "test", "booking", "1234");
        List<SMSNotification> notifications = IteratorUtils.toList(smsNotificationDAO.findAll().iterator());
        assertEquals(1, notifications.size());
    }
}