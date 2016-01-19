package com.mybus.controller;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.User;

/**
 * Created by schanda on 1/17/16.
 */


public class LayoutControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private LayoutDAO layoutDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        layoutDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testGetDefaultLayoutForSemiSleeper() throws Exception {
        JSONObject layoutType = new JSONObject();
        layoutType.put("layoutType", "AC_SEMI_SLEEPER");
        String str = layoutType.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/layout/default/%s", "AC_SEMI_SLEEPER"))
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.active").value(true));
        actions.andExpect(jsonPath("$.type").value("AC_SEMI_SLEEPER"));
        
        // validating front seats
        actions.andExpect(jsonPath("$.rows[0].seats[0].displayName").value("D"));
        actions.andExpect(jsonPath("$.rows[0].seats[0].display").value(true));
        actions.andExpect(jsonPath("$.rows[0].seats[0].window").value(true));
        actions.andExpect(jsonPath("$.rows[0].seats[0].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[1].seats[0].displayName").value("C"));
        actions.andExpect(jsonPath("$.rows[1].seats[0].display").value(true));
        actions.andExpect(jsonPath("$.rows[1].seats[0].window").value(false));
        actions.andExpect(jsonPath("$.rows[1].seats[0].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[2].seats[0].displayName").value(""));
        actions.andExpect(jsonPath("$.rows[2].seats[0].display").value(false));
        actions.andExpect(jsonPath("$.rows[2].seats[0].window").value(false));
        actions.andExpect(jsonPath("$.rows[2].seats[0].active").value(false));
        
        actions.andExpect(jsonPath("$.rows[3].seats[0].displayName").value("B"));
        actions.andExpect(jsonPath("$.rows[3].seats[0].display").value(false));
        actions.andExpect(jsonPath("$.rows[3].seats[0].window").value(false));
        actions.andExpect(jsonPath("$.rows[3].seats[0].active").value(false));
        
        actions.andExpect(jsonPath("$.rows[4].seats[0].displayName").value("A"));
        actions.andExpect(jsonPath("$.rows[4].seats[0].display").value(true));
        actions.andExpect(jsonPath("$.rows[4].seats[0].window").value(true));
        actions.andExpect(jsonPath("$.rows[4].seats[0].active").value(true));
        
     // validating front seats
        actions.andExpect(jsonPath("$.rows[0].seats[10].displayName").value("R19"));
        actions.andExpect(jsonPath("$.rows[0].seats[10].display").value(true));
        actions.andExpect(jsonPath("$.rows[0].seats[10].window").value(true));
        actions.andExpect(jsonPath("$.rows[0].seats[10].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[1].seats[10].displayName").value("R20"));
        actions.andExpect(jsonPath("$.rows[1].seats[10].display").value(true));
        actions.andExpect(jsonPath("$.rows[1].seats[10].window").value(false));
        actions.andExpect(jsonPath("$.rows[1].seats[10].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[2].seats[10].displayName").value("M21"));
        actions.andExpect(jsonPath("$.rows[2].seats[10].display").value(true));
        actions.andExpect(jsonPath("$.rows[2].seats[10].window").value(false));
        actions.andExpect(jsonPath("$.rows[2].seats[10].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[3].seats[10].displayName").value("L20"));
        actions.andExpect(jsonPath("$.rows[3].seats[10].display").value(true));
        actions.andExpect(jsonPath("$.rows[3].seats[10].window").value(false));
        actions.andExpect(jsonPath("$.rows[3].seats[10].active").value(true));
        
        actions.andExpect(jsonPath("$.rows[4].seats[10].displayName").value("L19"));
        actions.andExpect(jsonPath("$.rows[4].seats[10].display").value(true));
        actions.andExpect(jsonPath("$.rows[4].seats[10].window").value(true));
        actions.andExpect(jsonPath("$.rows[4].seats[10].active").value(true));
    }

}