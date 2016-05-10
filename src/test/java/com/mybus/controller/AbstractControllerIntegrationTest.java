package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.config.ApplicationDataTestConfig;
import com.mybus.config.CoreAppConfig;
import com.mybus.config.WebApplicationConfig;
import com.mybus.model.User;
import com.mybus.service.SessionManager;
import lombok.Getter;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import static java.lang.String.format;

@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = { CoreAppConfig.class, WebApplicationConfig.class, ApplicationDataTestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractControllerIntegrationTest.class);

    @Autowired
    @Getter
    private ObjectMapper objectMapper;

    @Autowired
    @Getter
    private WebApplicationContext wac;

    public static MockHttpServletRequestBuilder asUser(final MockHttpServletRequestBuilder request, final User user) {
        if (logger.isTraceEnabled()) {
            logger.trace(format("Mocking request as user: %s", user));
        }
        return request.requestAttr(SessionManager.USER_SESSION_ATTR, user);
    }


}
