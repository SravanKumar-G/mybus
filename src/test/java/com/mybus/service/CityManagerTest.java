package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.User;
import com.mybus.service.CityManager;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

/**
 * Created by skandula on 12/27/15.
 */
public class CityManagerTest extends AbstractControllerIntegrationTest{

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Autowired
    private CityManager cityManager;
    
    @Before
    public void setup() {
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }


    @Test
    public void testAddBoardingPointToCity() throws Exception {
        City city = new City("TextCity", "TestState", new HashSet<>());
        city = cityDAO.save(city);
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123");
        Assert.assertEquals(0, city.getBoardingPoints().size());
        city = cityManager.addBoardingPointToCity(city.getId(), bp);
        Assert.assertEquals(1, city.getBoardingPoints().size());
        Assert.assertNotNull(city.getBoardingPoints().iterator().next().getId());
    }

    @Test
    public void testUpdateBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", new HashSet<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123");
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        Assert.assertEquals(1, city.getBoardingPoints().size());
        Assert.assertNotNull(city.getBoardingPoints().iterator().next().getId());
        bp.setContact("1234");
        city = cityManager.updateBoardingPoint(city.getId(), bp);
        Assert.assertEquals(1, city.getBoardingPoints().size());
        Assert.assertNotNull(city.getBoardingPoints().iterator().next().getId());
        Assert.assertEquals("1234", city.getBoardingPoints().iterator().next().getContact());
    }

    @Test
    public void testDeleteBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", new HashSet<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123");
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        Assert.assertEquals(1, city.getBoardingPoints().size());
        Assert.assertNotNull(city.getBoardingPoints().iterator().next().getId());
        city = cityManager.deleteBoardingPoint(city.getId(), bp.getId());
        Assert.assertEquals(0, city.getBoardingPoints().size());
    }
}