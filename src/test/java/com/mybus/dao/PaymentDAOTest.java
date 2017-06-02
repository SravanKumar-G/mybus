package com.mybus.dao;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.model.Payment;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by busda001 on 5/15/17.
 */
public class PaymentDAOTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    @Before
    public void clean(){
        paymentDAO.deleteAll();
    }


    @Test
    public void testCreatePayments(){
        for(int i =0;i< 20;i++) {
            Payment payment = new Payment();
            payment.setName("Books Bill");
            if(i%3 == 0) {
                payment.setStatus("Approved");
            }
            payment.setAmount(2000);
            paymentDAO.save(payment);
        }

        List<Payment> payments = IteratorUtils.toList(paymentDAO.findAll().iterator());
        Assert.assertEquals(20, payments.size());
        Page<Payment> page = paymentMongoDAO.findPendingPayments(null);
        Assert.assertEquals(13, page.getContent().size());



    }
}
