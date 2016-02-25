package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.AgentPlanTypeDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.AgentPlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentPlanTypeManager {
    private static final Logger logger = LoggerFactory.getLogger(AgentPlanTypeManager.class);

    @Autowired
    private AgentPlanTypeDAO agentPlanTypeDAO;

    public AgentPlanType saveAgentPlanType(AgentPlanType agentPlanType){
        Preconditions.checkNotNull(agentPlanType, "The plan type can not be null");
        Preconditions.checkNotNull(agentPlanType.getName(), "Name can not be null");
        Preconditions.checkNotNull(agentPlanType.getType(), "Type can not be null");
        Preconditions.checkNotNull(agentPlanType.getCommissionType(), "Commission can not be null");
        Preconditions.checkNotNull(agentPlanType.getDeposit(), "Deposit can not be null");
        Preconditions.checkNotNull(agentPlanType.getSettlementFrequency(), "settlement Frequency can not be null");
        //AgentPlanType duplicateAgentPlanType = agentPlanTypeDAO.findOneByName(agentPlanType.getName());

        return agentPlanTypeDAO.save(agentPlanType);
    }
}
