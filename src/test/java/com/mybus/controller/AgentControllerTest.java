package com.mybus.controller;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Agent;
import com.mybus.model.User;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 3/8/17.
 */
public class AgentControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private UserDAO userDAO;

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
        agentDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    public void testGetAgentsPage() throws Exception {
        for(int i=0; i<40; i++) {
            agentDAO.save(new Agent());
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/agents?page=3&size=5&sort=name"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.total").value(40));
        actions.andExpect(jsonPath("$.data.content").isArray());
        actions.andExpect(jsonPath("$.data.content", Matchers.hasSize(5)));
    }
}