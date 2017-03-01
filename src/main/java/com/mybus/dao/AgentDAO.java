package com.mybus.dao;

import com.mybus.model.Agent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentDAO extends PagingAndSortingRepository<Agent, String> {
    Iterable<Agent> findByBranchName(String branchName);
    Iterable<Agent> findByUsername(String username);
}
