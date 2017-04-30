package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.OfficeExpense;
import com.mybus.model.Payment;
import com.mybus.model.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by busda001 on 4/29/17.
 */
public class OfficeExpenseControllerTest extends AbstractControllerIntegrationTest{
    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;


    private User currentUser;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        userDAO.deleteAll();
        officeExpenseDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void findPendingExpenses() throws Exception {
        for(int i=0;i<10;i++){
            OfficeExpense officeExpense = new OfficeExpense();
            officeExpense.setDescription("Testing "+ i);
            if(i%2 == 0) {
                officeExpense.setStatus(Payment.STATUS_APPROVED);
            }
            officeExpenseDAO.save(officeExpense);
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/officeExpenses/pending"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));

        actions = mockMvc.perform(asUser(get("/api/v1/officeExpenses/approved"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));
    }

}