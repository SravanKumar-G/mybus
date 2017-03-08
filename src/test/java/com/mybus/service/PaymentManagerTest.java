package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
/**
 * Created by srinikandula on 3/8/17.
 */
public class PaymentManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private PaymentManager paymentManager;

    @After
    @Before
    public void cleanup() {
        paymentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
    }

    @Test
    public void testUpdatePayment() {
        BranchOffice branchOffice1 = branchOfficeDAO.save(new BranchOffice());

        BranchOffice branchOffice2 = new BranchOffice();
        branchOffice2.setCashBalance(5000);
        branchOffice2 = branchOfficeDAO.save(branchOffice2);
        Payment payment1 = new Payment();
        payment1.setBranchOfficeId(branchOffice1.getId());
        payment1.setType(PaymentType.INCOME);
        payment1.setAmount(1000);
        Payment payment2 = new Payment();
        payment2.setType(PaymentType.EXPENSE);
        payment2.setBranchOfficeId(branchOffice2.getId());
        payment2.setAmount(2000);

        paymentManager.updatePayment(payment1);
        paymentManager.updatePayment(payment2);
        branchOffice1 = branchOfficeDAO.findOne(branchOffice1.getId());
        branchOffice2 = branchOfficeDAO.findOne(branchOffice2.getId());
        assertEquals(0, branchOffice1.getCashBalance(), 0.0);
        assertEquals(5000, branchOffice2.getCashBalance(), 0.0);
        payment1.setStatus(Payment.STATUS_APPROVED);
        payment2.setStatus(Payment.STATUS_APPROVED);
        payment1 = paymentManager.updatePayment(payment1);
        payment2 = paymentManager.updatePayment(payment2);
        branchOffice1 = branchOfficeDAO.findOne(branchOffice1.getId());
        branchOffice2 = branchOfficeDAO.findOne(branchOffice2.getId());
        assertEquals(1000, branchOffice1.getCashBalance(), 0.0);
        assertEquals(3000, branchOffice2.getCashBalance(), 0.0);
    }
}