package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.model.Agent;
import com.mybus.model.BranchOffice;
import com.mybus.model.Shipment;
import org.apache.commons.collections.IteratorUtils;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;


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

    public long count(String query, boolean showInvalid) {
        return agentMongoDAO.countAgents(query, showInvalid);
    }

}