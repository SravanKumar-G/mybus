package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import com.mybus.model.User;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import sun.util.resources.cldr.ebu.CalendarData_ebu_KE;

import java.text.ParseException;
import java.util.Calendar;

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

    @Autowired
    private SessionManager sessionManager;

    private User currentUser;

    @Autowired
    private UserDAO userDAO;

    @After
    @Before
    public void cleanup() {
        paymentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
        sessionManager.setCurrentUser(currentUser);
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
        payment1.setDescription("Testing");
        payment1.setAmount(5000);
        Payment payment2 = new Payment();
        payment2.setDescription("Testing");
        payment2.setType(PaymentType.EXPENSE);
        payment2.setBranchOfficeId(branchOffice2.getId());
        payment2.setAmount(2000);
        currentUser = userDAO.save(currentUser);
        payment1.setCreatedBy(currentUser.getId());
        payment2.setCreatedBy(currentUser.getId());

        sessionManager.setCurrentUser(currentUser);
        payment1 = paymentManager.updatePayment(payment1);
        payment2 = paymentManager.updatePayment(payment2);
        currentUser.setAmountToBePaid(1000);
        currentUser = userDAO.save(currentUser);
        assertEquals(1000, currentUser.getAmountToBePaid(), 0.0);
        payment1.setStatus(Payment.STATUS_APPROVED);
        payment2.setStatus(Payment.STATUS_APPROVED);
        payment1.setCreatedBy(currentUser.getId());
        payment2.setCreatedBy(currentUser.getId());
        paymentManager.updatePayment(payment1);
        paymentManager.updatePayment(payment2);
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(4000, currentUser.getAmountToBePaid(), 0.0);

    }

    /**
     * Find payments by date
     */
    @Test
    public void testFindPayments() throws ParseException {
        Calendar calender = Calendar.getInstance();
        for(int i=1; i<=50; i++) {
            Payment payment = new Payment();
            payment.setDate(ServiceConstants.parseDate(ServiceConstants.formatDate(calender.getTime())));
            paymentDAO.save(payment);
            if(i%5 == 0) {
                calender.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        calender = Calendar.getInstance();
        JSONObject query = new JSONObject();
        query.put("startDate", ServiceConstants.formatDate(calender.getTime()));
        Page<Payment> page  = paymentManager.findPayments(query, null);
        assertEquals(50, page.getTotalElements());
        calender.add(Calendar.DAY_OF_MONTH, 1);
        query.put("endDate", ServiceConstants.formatDate(calender.getTime()));
        page = paymentManager.findPayments(query, null);
        assertEquals(10, page.getTotalElements());
    }
}