package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.model.ServiceExpense;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Date;
import static org.junit.Assert.*;

public class ServiceExpenseManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceExpenseManager serviceExpenseManager;

    @Before
    @After
    public void cleanup(){
        serviceExpenseDAO.deleteAll();
    }

    @Test
    public void testSave() {
        ServiceExpense serviceExpense = new ServiceExpense();
        serviceExpense.setServiceId("123");
        serviceExpense.setJourneyDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        serviceExpense = serviceExpenseManager.save(serviceExpense);
        assertNotNull(serviceExpense);
    }

}