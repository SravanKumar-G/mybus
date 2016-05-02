package com.mybus.controller;

import com.mybus.dao.RoleDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Role;
import com.mybus.model.User;
import com.mybus.service.RoleManager;

import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 5/1/16.
 */
public class RoleControllerTest  extends AbstractControllerIntegrationTest{
    private MockMvc mockMvc;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Before
    public void before(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
      //  cleanup();
        currentUser = userDAO.save(currentUser);
        cleanup();
    }

    @After
    public void after(){
        cleanup();
    }

    private void cleanup() {
        roleDAO.deleteAll();
        userDAO.deleteAll();
    }
    @Test
    public void testGetRoles() throws Exception {
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/roles"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(0)));
        for(int i=0;i<3;i++){
            roleDAO.save(new Role("Test"+i));
        }
        actions = mockMvc.perform(asUser(get("/api/v1/roles"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void testCreateRole() throws Exception {
        JSONObject role = new JSONObject();
        //fail case
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(jsonPath("$.message").value("role name can not be null"));
        //success
        role.put("name", "testRole");
        actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(role.get("name")));

        actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(jsonPath("$.message").value("Role  already exists with this same name"));

    }

    @Test
    public void testUpdateRole() throws Exception {

    }

    @Test
    public void testDeleteRole() throws Exception {

    }

    @Test
    public void testGetRole() throws Exception {

    }
}