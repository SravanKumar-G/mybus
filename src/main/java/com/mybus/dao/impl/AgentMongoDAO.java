package com.mybus.dao.impl;

import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class AgentMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public List<String> findAgentNamesByOfficeId(String officeId) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("operatorId").is(sessionManager.getOperatorId()));
        if(officeId != null) {
            query.addCriteria(where("branchOfficeId").is(officeId));
        }
        List<Agent> agents = mongoTemplate.find(query, Agent.class);
        List<String> namesList = agents.stream()
                .map(Agent::getUsername)
                .collect(Collectors.toList());
        return namesList;
    }

    public List<Agent> findAgents(String nameQuery, boolean showInvalid) {
        List<Agent> agents = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("operatorId").is(sessionManager.getOperatorId()));
        if(nameQuery != null) {
            query.addCriteria(Criteria.where("username").regex(nameQuery));
        }
        if(showInvalid){
            query.addCriteria(Criteria.where("branchOfficeId").exists(false));
        }
        query.fields().include("username");
        query.fields().include("branchOfficeId");
        agents = mongoTemplate.find(query, Agent.class);
        return agents;
    }

    public Iterable<Agent> findAgents(String key, boolean showInvalid, Pageable pageable) {
        Iterable<Agent> agents = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("operatorId").is(sessionManager.getOperatorId()));

        if(key != null) {
            query.addCriteria(Criteria.where("username").regex(key));
        }
        if(showInvalid){
            query.addCriteria(Criteria.where("branchOfficeId").exists(false));
        }
        if(pageable != null){
            query.with(pageable);
        }
        agents = mongoTemplate.find(query, Agent.class);
        return agents;
    }
    public long countAgents(String key, boolean showInvalid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("operatorId").is(sessionManager.getOperatorId()));

        if(key != null) {
            query.addCriteria(Criteria.where("username").regex(key));
        }
        if(showInvalid){
            query.addCriteria(Criteria.where("branchOfficeId").exists(false));
        }
        return mongoTemplate.count(query, Agent.class);
    }
}
