package com.mybus.dao;

import com.mybus.model.Route;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by skandula on 12/30/15.
 */
@Repository
public interface RouteDAO extends PagingAndSortingRepository<Route, String> {
    Route findByName(String name);
    List<Route> findByActive(boolean active);
}
