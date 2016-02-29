package com.mybus.dao;

import com.mybus.model.AgentPlanType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by svanik on 2/22/2016.
 */
public interface AgentPlanTypeDAO extends PagingAndSortingRepository<AgentPlanType, String> {

    AgentPlanType findOneById(String Id);
}
