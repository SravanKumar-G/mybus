package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.City;
import com.mybus.model.User;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 12/9/15.
 */
public class CityControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testCreateCity() throws Exception {
        JSONObject city = new JSONObject();
        city.put("name", "city");
        city.put("state", "CA");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value("city"));

    }
}