package com.mybus.dao.impl;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Created by srinikandula on 3/2/17.
 */
public class AgentMongoDAOTest extends AbstractControllerIntegrationTest {

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Before
    @After
    public void cleanup() {
        agentDAO.deleteAll();
    }

    @Test
    public void testFindAgentNamesByOfficeId() throws Exception {
        for(int i=0; i<5; i++) {
            Agent agent = new Agent();
            agent.setName("name"+i);
            if(i == 2) {
                agent.setBranchOfficeId("1232");
            } else {
                agent.setBranchOfficeId("123");
            }
            agentDAO.save(agent);
        }
        List<String> agents = agentMongoDAO.findAgentNamesByOfficeId("123");
        assertEquals(4, agents.size());
    }
}