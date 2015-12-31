package com.mybus.dao;

import com.mybus.model.City;
import com.mybus.model.Route;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by skandula on 12/30/15.
 */
public interface RouteDAO extends PagingAndSortingRepository<Route, String> {
    Route findByName(String name);
    List<Route> findByActive(boolean active);
}
