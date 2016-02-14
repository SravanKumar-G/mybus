package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.controller.UserController;
import com.mybus.dao.UserDAO;
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
        } else {
            logger.debug("Skipping test data creation");
        }
    }
}
