package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.test.util.AmenityTestService;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

/**
 * Created by skandula on 12/30/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class})
@WebAppConfiguration
public class RouteManagerTest {
    @Autowired
    private RouteDAO routeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private AmenityTestService.RouteTestService routeTestService;

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
    public void testSaveRouteWithInvalidFromCityId() {
        Route route = new Route("Name", "123", "1234", new LinkedHashSet<>(), false);
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid from city id");
        routeManager.saveRoute(route);
    }
    @Test
    public void testSaveRouteWithInvalidToCityId() {
        Route route = new Route("Name", "123", "1234", new LinkedHashSet<>(), false);
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        route.setFromCityId(fromCity.getId());
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid to city id");
        routeManager.saveRoute(route);
    }
    @Test
    public void testSaveRoute() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        Preconditions.checkNotNull(savedRoute);
        routeManager.saveRoute(savedRoute);
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
    }
    @Test
    public void testSaveRouteWithDuplicateName() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        //try saving the route with same name
        savedRoute.setId(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Route with the same name exits");
        routeManager.saveRoute(savedRoute);
    }
    @Test
    public void testSaveRouteWithNewName() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        //try saving the route with same name
        savedRoute.setId(null);
        //change the name and it should be good
        savedRoute.setName(savedRoute.getName() + "_New");
        routeManager.saveRoute(savedRoute);
        List<Route> routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(2, routes.size());
        List cities =IteratorUtils.toList(cityDAO.findAll().iterator());
        Assert.assertEquals(2, cities.size());
        List activeRoutes = IteratorUtils.toList(routeDAO.findByActive(true).iterator());
        Assert.assertEquals(2, activeRoutes.size());
        List inActiveRoutes = IteratorUtils.toList(routeDAO.findByActive(false).iterator());
        Assert.assertEquals(0, inActiveRoutes.size());
    }

    @Test
    public void testDeactivateRoute() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deactiveRoute("123");
        Route route = routeManager.saveRoute(routeTestService.createTestRoute());
        route = routeManager.deactiveRoute(route.getId());
        Assert.assertEquals("Deactivation failed", false, route.isActive());
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
        List activeRoutes = IteratorUtils.toList(routeDAO.findByActive(true).iterator());
        Assert.assertEquals(0, activeRoutes.size());
        List inActiveRoutes = IteratorUtils.toList(routeDAO.findByActive(false).iterator());
        Assert.assertEquals(1, inActiveRoutes.size());
    }

    @Test
    public void testDeleteRoute() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deleteRoute("123");
        Route route = routeManager.saveRoute(routeTestService.createTestRoute());
        routeManager.deleteRoute(route.getId());
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(0, routes.size());
    }


    @Test
    public void createTestRouteWithInvalidViaCities() {
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
        Route route = new Route("Name", fromCity.getId(), toCity.getId(), new LinkedHashSet<>(), false);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("invalid via city is found in via cities");
        route.getViaCities().add("123");
        routeManager.saveRoute(route);
    }

    @Test
    public void createTestRouteWithValidViaCities() {
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
        Route route = new Route("Name", fromCity.getId(), toCity.getId(), new LinkedHashSet<>(), false);
        for(int i =0; i<3; i++){
            route.getViaCities().add(cityManager.saveCity(new City("TestCity"+i, "TestState", true, new ArrayList<>())).getId());
        }
        routeManager.saveRoute(route);
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
        List cities = IteratorUtils.toList(cityDAO.findAll().iterator());
        Assert.assertEquals(5, cities.size());
    }

}