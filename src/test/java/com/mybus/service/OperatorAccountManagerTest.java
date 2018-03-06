package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

public class OperatorAccountManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private OperatorAccountManager operatorAccountManager;

    @Before
    @After
    public void cleanup(){
        operatorAccountDAO.deleteAll();
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testSave(){
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount("jagan", "jagantravels.com",
                "jagan.com","bitla","srini","passwro",true));
        operatorAccountManager.saveAccount(operatorAccount);
        operatorAccount.setName("New");
        expectedEx.expect(BadRequestException.class);
        operatorAccountManager.saveAccount(new OperatorAccount("jagan", "jagantravels.com",
                "jagan.com","bitla","srini","passwro",true));

    }

}