package com.mybus.dao;

import com.mybus.model.Agent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
public interface AgentDAO extends PagingAndSortingRepository<Agent, String> {
    Iterable<Agent> findByBranchName(String branchName);
    //Agent findByUsername(String username);
    Agent findByUsername(String username);
    Iterable<Agent> findByBranchOfficeId(String officeId);

}
