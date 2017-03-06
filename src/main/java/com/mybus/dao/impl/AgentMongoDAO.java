package com.mybus.dao.impl;

import com.mybus.model.Agent;
import com.mybus.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    public List<String> findAgentNamesByOfficeId(String officeId) {
        final Query query = new Query();
        //query.fields().include("username");
        query.addCriteria(where("branchOfficeId").is(officeId));
        List<Agent> agents = mongoTemplate.find(query, Agent.class);
        List<String> namesList = agents.stream()
                .map(Agent::getUsername)
                .collect(Collectors.toList());
        return namesList;
    }
}
