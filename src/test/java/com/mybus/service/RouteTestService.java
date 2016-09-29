package com.mybus.service;

import com.mybus.model.City;
import com.mybus.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by srinikandula on 9/25/16.
 */
@Service
public class RouteTestService {
    @Autowired
    private RouteManager routeManager;

    @Autowired
    private CityManager cityManager;

    public Route createTestRoute() {
        Route route = new Route("Name", "123", "1234", new LinkedHashSet<>(), true);
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        route.setFromCityId(fromCity.getId());

        City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
        route.setToCityId(toCity.getId());
        return routeManager.saveRoute(route);
    }
}
