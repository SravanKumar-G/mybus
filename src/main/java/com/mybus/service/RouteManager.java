package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.model.Route;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 12/30/15.
 */
@Service
public class RouteManager {
    private static final Logger logger = LoggerFactory.getLogger(RouteManager.class);

    @Autowired
    private RouteDAO routeDAO;

    @Autowired
    private CityDAO cityDAO;

    public Route saveRoute(Route route) {
        Preconditions.checkNotNull(route, "route can not be null");
        Preconditions.checkNotNull(route.getName(), "Route name can not be null");
        Preconditions.checkNotNull(route.getFromCity(), "Route from city can not be null");
        Preconditions.checkNotNull(route.getToCity(), "Route to city can not be null");
        Preconditions.checkNotNull(cityDAO.findOne(route.getFromCity()), "Invalid from city id");
        Preconditions.checkNotNull(cityDAO.findOne(route.getToCity()), "Invalid to city id");
        if(StringUtils.isBlank(route.getId()) && routeDAO.findByName(route.getName()) != null) {
            throw new RuntimeException("Route with the same name exits");
        }
        route.getViaCities().stream().forEach(c -> {
            if(cityDAO.findOne(c) == null) {
                throw new RuntimeException("invalid via city is found in via cities");
            }
        });
        return routeDAO.save(route);
    }
    public void deleteRoute(String routeId) {
        Preconditions.checkNotNull(routeId);
        Preconditions.checkNotNull(routeDAO.findOne(routeId), "Invalid Route id");
        routeDAO.delete(routeId);
    }

    public Route deactiveRoute(String routeId) {
        Preconditions.checkNotNull(routeId);
        Route route = routeDAO.findOne(routeId);
        Preconditions.checkNotNull(route, "Invalid Route id");
        Preconditions.checkArgument(!route.isActive(), "Route is already inactive");
        route.setActive(false);
        return routeDAO.save(route);
    }
}
