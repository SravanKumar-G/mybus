package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.dto.AgentNameDTO;
import com.mybus.model.Agent;
import com.mybus.model.BranchOffice;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private BookingManager bookingManager;

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

    /**
     * Save an agent. This should check if there is any bookings that are invalid, if found make them valid and then
     * check if the service for the bookings need to be validated.
     *
     * @param agent
     * @return
     */
    public Agent save(Agent agent) {
        bookingManager.validateAgentBookings(agent);
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

    /**
     *
     * @param query
     * @param showInvalid
     * @param pageable
     * @return
     */
    public Page<Agent> findAgents(String query, boolean showInvalid,Pageable pageable) {
        long total = (count(query, showInvalid));
        List<Agent> agents = IteratorUtils.toList(agentMongoDAO.findAgents(query, showInvalid, pageable).iterator());
        Map<String, String> namesMap = branchOfficeManager.getNamesMap();
        agents.stream().forEach(agent -> {
            agent.getAttributes().put(BranchOffice.KEY_NAME, namesMap.get(agent.getBranchOfficeId()));
        });
        Page<Agent> page = new PageImpl<Agent>(agents, pageable, total);
        return page;
    }
    public long count(String query, boolean showInvalid) {
        return agentMongoDAO.countAgents(query, showInvalid);
    }

    public Iterable<AgentNameDTO> getAgentNames() {
        String[] fields = {"username"};
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE, new Sort("username"));
        Iterable<Agent> agents = mongoQueryDAO.getDocuments(Agent.class, "agent", fields, null, pageable);
        List<AgentNameDTO> agentNames = new ArrayList<>();
        agents.forEach(agent -> {
                    agentNames.add(new AgentNameDTO(agent.getId(), agent.getUsername()));
                });
        return agentNames;
    }

}