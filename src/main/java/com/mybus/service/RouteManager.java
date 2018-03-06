package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.impl.RouteMongoDAO;
import com.mybus.model.Route;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.*;
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

    @Autowired
    private RouteMongoDAO routeMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    public Route saveRoute(Route route) {
        route.setOperatorId(sessionManager.getOperatorId());
        validateRoute(route);
        return routeDAO.save(route);
    }
    public boolean deleteRoute(String routeId) {
        Preconditions.checkNotNull(routeId);
        Preconditions.checkNotNull(routeDAO.findOne(routeId), "Invalid Route id");
        routeDAO.delete(routeId);
        //TODO: check if there is any active services, if found throw an error.

        return true;
    }

    public Route deactiveRoute(String routeId) {
        Preconditions.checkNotNull(routeId);
        Route route = routeDAO.findOne(routeId);
        Preconditions.checkNotNull(route, "Invalid Route id");
        Preconditions.checkArgument(!route.isActive(), "Route is already inactive");
        route.setActive(false);
        return routeDAO.save(route);
    }

    public boolean update(Route route) {
        validateRoute(route);
        Route r = routeDAO.findOne(route.getId());
        Preconditions.checkNotNull(route, "No route found to update");
        try {
            r.merge(route);
            routeDAO.save(r);
        } catch (Exception e) {
           logger.error("Error updating the Route ", e);
           throw new RuntimeException(e);
        }
        return true;
    }

    private void validateRoute(final Route route) {
        Preconditions.checkNotNull(route, "route can not be null");
        if(route == null){
            throw new NullPointerException("route can not be null");
        }
        Preconditions.checkNotNull(route.getName(), "Route name can not be null");
        Preconditions.checkNotNull(route.getFromCityId(), "Route from city can not be null");
        Preconditions.checkNotNull(route.getToCityId(), "Route to city can not be null");
        Preconditions.checkNotNull(cityDAO.findOne(route.getFromCityId()), "Invalid from city id");
        Preconditions.checkNotNull(cityDAO.findOne(route.getToCityId()), "Invalid to city id");
        if(StringUtils.isBlank(route.getId()) && routeDAO.findByName(route.getName()) != null) {
            throw new RuntimeException("Route with the same name exits");
        }
        route.getViaCities().stream().forEach(c -> {
            if(cityDAO.findOne(c) == null) {
                throw new RuntimeException("invalid via city is found in via cities");
            }
        });
    }

    public long count() {
        return routeMongoDAO.count();
    }

    public Route findOne(String id) {
        return routeDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId());
    }

    public List<Route> findAll(Pageable pageable) {
        return routeDAO.findByOperatorId(sessionManager.getOperatorId());
    }
}
