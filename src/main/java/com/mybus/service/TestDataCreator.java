package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.controller.UserController;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.PaymentGateway;
import com.mybus.model.PaymentGateways;
import com.mybus.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 12/12/15.
 */
@Service
public class TestDataCreator {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PaymentGatewayDAO paymentGatewayDAO;

    @Autowired
    private SystemProperties systemProperties;

    public void createTestData() {
        String createTestData = systemProperties.getProperty("create.test.data");
        if(StringUtils.isNotBlank(createTestData) && Boolean.parseBoolean(createTestData)) {
            User user = userDAO.findOneByUserName("bill");
            if(user == null) {
                user = new User();
                user.setFirstName("bill");
                user.setActive(true);
                user.setAdmin(true);
                user.setLastName("Lname");
                user.setPassword("123");
                user.setUserName("bill");
                userDAO.save(user);
            } else {
                logger.debug("Test data already created");
            }
            PaymentGateway payuGateway = paymentGatewayDAO.findByName("PAYU");
            if(payuGateway == null) {
                PaymentGateway pg = new PaymentGateway();
                pg.setApiKey("eCwWELxi"); //payu  salt
                pg.setAccountId("gtKFFx"); //payu key
                pg.setGetwayUrl("https://test.payu.in/_payment");
                pg.setPaymentType("PG");
                pg.setName("PAYU");
                paymentGatewayDAO.save(pg);
            }
            payuGateway = paymentGatewayDAO.findByName("EBS");
            if(payuGateway == null) {
                PaymentGateway pg = new PaymentGateway();
                pg.setApiKey("ebs key"); //ebs secret key
                pg.setAccountId("gtKFFx"); //ebs accountId
                pg.setGetwayUrl("https://secure.ebs.in/pg/ma/payment/request");
                pg.setPaymentType("PG");
                pg.setName("EBS");
                paymentGatewayDAO.save(pg);
            }
        } else {
            logger.debug("Skipping test data creation");
        }
    }
}
