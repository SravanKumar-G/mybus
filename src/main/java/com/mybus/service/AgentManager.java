package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.model.Agent;
import com.mybus.model.BranchOffice;
import com.mybus.model.ResponseData;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class AgentManager {

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    public Agent getAgent(String agentId) {
        Agent agent = agentDAO.findOne(agentId);
        if(agent.getBranchOfficeId() != null) {
            BranchOffice branchOffice = branchOfficeDAO.findOne(agent.getBranchOfficeId());
            if(branchOffice != null) {
                agent.getAttributes().put(BranchOffice.KEY_NAME, branchOffice.getName());
            }
        }
        return agent;
    }

    public Agent save(Agent agent) {
        return agentDAO.save(agent);
    }

    public Iterable<Agent> findAgents(String query, boolean showInvalid) {
        List<Agent> agents = IteratorUtils.toList(agentMongoDAO.findAgents(query, showInvalid).iterator());
        Map<String, String> namesMap = branchOfficeManager.getNamesMap();
        agents.stream().forEach(agent -> {
            agent.getAttributes().put(BranchOffice.KEY_NAME, namesMap.get(agent.getBranchOfficeId()));
        });
        return agents;
    }

    public ResponseData<Agent> findAgents(String query, boolean showInvalid,Pageable pageable) {
        ResponseData<Agent> responseData = new ResponseData<>();
        responseData.setTotal(count(query, showInvalid));
        List<Agent> agents = IteratorUtils.toList(agentMongoDAO.findAgents(query, showInvalid, pageable).iterator());
        Map<String, String> namesMap = branchOfficeManager.getNamesMap();
        agents.stream().forEach(agent -> {
            agent.getAttributes().put(BranchOffice.KEY_NAME, namesMap.get(agent.getBranchOfficeId()));
        });
        return responseData;
    }
    public long count(String query, boolean showInvalid) {
        return agentMongoDAO.countAgents(query, showInvalid);
    }

}