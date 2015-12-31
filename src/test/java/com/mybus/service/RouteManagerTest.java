package com.mybus.service;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.model.User;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by skandula on 12/30/15.
 */
public class RouteManagerTest extends AbstractControllerIntegrationTest {
    @Autowired
    private RouteDAO routeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private RouteManager routeManager;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        routeDAO.deleteAll();
        cityDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testSaveRoute() {
        Route route = new Route("Name", "123", "1234", new LinkedHashSet<>(), false);
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid from city id");
        routeManager.saveRoute(route);
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", new HashSet<>()));
        route.setFromCity(fromCity.getId());
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid to city id");
        routeManager.saveRoute(route);
        City toCity = cityManager.saveCity(new City("TestCity", "TestState", new HashSet<>()));
        route.setFromCity(toCity.getId());
        Route savedRoute = routeManager.saveRoute(route);
        Preconditions.checkNotNull(savedRoute);
        routeManager.saveRoute(route);
        List routes = Lists.newArrayList(routeDAO.findAll());
        Assert.assertEquals(1, routes.size());

        //try saving the route with same name
        savedRoute.setId(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Route with the same name exits");
        routeManager.saveRoute(savedRoute);
        //change the name and it should be good
        savedRoute.setName(savedRoute.getName() + "_New");
        routeManager.saveRoute(savedRoute);
        routes = Lists.newArrayList(routeDAO.findAll());
        Assert.assertEquals(2, routes.size());
        List cities = Lists.newArrayList(cityDAO.findAll());
        Assert.assertEquals(2, cities.size());
        List activeRoutes = Lists.newArrayList(routeDAO.findByActive(true));
        Assert.assertEquals(2, activeRoutes.size());
        List inActiveRoutes = Lists.newArrayList(routeDAO.findByActive(false));
        Assert.assertEquals(0, inActiveRoutes.size());
    }

    @Test
    public void testDeactivateRoute() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deactiveRoute("123");
        Route route = createTestRoute();
        route = routeManager.deactiveRoute(route.getId());
        Assert.assertEquals("Deactivation failed", false, route.isActive());
        List routes = Lists.newArrayList(routeDAO.findAll());
        Assert.assertEquals(1, routes.size());
        List activeRoutes = Lists.newArrayList(routeDAO.findByActive(true));
        Assert.assertEquals(0, activeRoutes.size());
        List inActiveRoutes = Lists.newArrayList(routeDAO.findByActive(false));
        Assert.assertEquals(1, inActiveRoutes.size());
    }

    @Test
    public void testDeleteRoute() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deleteRoute("123");
        Route route = createTestRoute();
        routeManager.deleteRoute(route.getId());
        List routes = Lists.newArrayList(routeDAO.findAll());
        Assert.assertEquals(0, routes.size());
    }
    /**
     * Create a route for testing
     * @return
     */
    private Route createTestRoute() {
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", new HashSet<>()));
        City toCity = cityManager.saveCity(new City("TestCity", "TestState", new HashSet<>()));
        Route route = new Route("Name", fromCity.getId(), toCity.getId(), new LinkedHashSet<>(), false);
        return routeManager.saveRoute(route);
    }
}