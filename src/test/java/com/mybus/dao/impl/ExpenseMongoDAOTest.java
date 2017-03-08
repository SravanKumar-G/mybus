package com.mybus.dao.impl;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.PaymentDAO;
import com.mybus.model.Payment;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
/**
 * Created by srinikandula on 3/7/17.
 */
public class ExpenseMongoDAOTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private PaymentMongoDAO expenseMongoDAO;
    @Before
    @After
    public void cleanup() {
        paymentDAO.deleteAll();
    }

    private void createTestData() {
        for(int i=0;i<5;i++) {
            Payment expense = new Payment();
            expense.setDescription("Test"+i);
            if(i == 2){
                DateTime dateTime = new DateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -3);
                expense.setDate(calendar.getTime());
            }else {
                expense.setDate(new Date());
            }
            paymentDAO.save(expense);
        }
    }

    @Test
    public void testCount() throws Exception {

        /*JSONObject query = new JSONObject();
        query.put("startDate", ServiceConstants.df.format(new Date()));
        createTestData();
        long count = expenseMongoDAO.count(query);
        assertEquals(4,count);
        */
    }

    /*
    @Test
    public void testFind() throws Exception {
        JSONObject query = new JSONObject();
        query.put("startDate",  ServiceConstants.df.format(new Date()));
        createTestData();
        List<Payment> expenses = IteratorUtils.toList(expenseMongoDAO.find(query).iterator());
        assertEquals(4, expenses.size());
    }*/
}