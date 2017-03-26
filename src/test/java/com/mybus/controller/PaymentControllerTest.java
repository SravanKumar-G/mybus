package com.mybus.controller;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import com.mybus.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 3/8/17.
 */
public class PaymentControllerTest  extends AbstractControllerIntegrationTest {

    @Autowired
    private PaymentDAO paymentDAO;
    private MockMvc mockMvc;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CityDAO cityDAO;

    private User currentUser;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    private Payment payment1,payment2;

    @Before
    public void setup() {
        cleanup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }

    @After
    public void cleanup(){
        paymentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }

    private void createTestData() {
        BranchOffice branchOffice1 = branchOfficeDAO.save(new BranchOffice());

        BranchOffice branchOffice2 = new BranchOffice();
        branchOffice2.setCashBalance(5000);
        branchOffice2 = branchOfficeDAO.save(branchOffice2);
        payment1 = new Payment();
        payment1.setBranchOfficeId(branchOffice1.getId());
        payment1.setType(PaymentType.INCOME);
        payment1.setAmount(1000);
        payment2 = new Payment();
        payment2.setType(PaymentType.EXPENSE);
        payment2.setBranchOfficeId(branchOffice2.getId());
        payment2.setAmount(2000);
        paymentDAO.save(payment1);
        paymentDAO.save(payment2);

    }
    @Test
    public void testGetUserInfo() throws Exception {

    }

    @Test
    public void testGetCount() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testUpdatePayment() throws Exception {
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/payment")
                .content(getObjectMapper().writeValueAsBytes(payment1)).contentType(MediaType.APPLICATION_JSON),
                currentUser));
        //actions.andExpect(status().isOk());
    }
}