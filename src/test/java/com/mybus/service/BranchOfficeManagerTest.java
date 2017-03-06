package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.City;
import com.mybus.model.User;
import com.mybus.test.util.UserTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/13/16.
 */
public class BranchOfficeManagerTest  extends AbstractControllerIntegrationTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private UserManager userManager;

    @Before
    @After
    public void cleanup() {
        userDAO.deleteAll();
        cityDAO.deleteAll();
        branchOfficeDAO.deleteAll();
    }

    @Test
    public void testFind() {
        List<City> cities = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for(int i =0; i<10; i++) {
            cities.add(cityDAO.save(new City("TestCity" + (i + 1), "TestState", true, new ArrayList<>())));
            User user = UserTestService.createNew();
            user.setActive(true);
            user.setLastName(user.getLastName()+i);
            user.setEmail(i+ user.getEmail());
            users.add(userDAO.save(user));
        }
        for(int i=0; i<10; i++) {
            int randomIndex = new Double(Math.random()*10).intValue();
            branchOfficeDAO.save(new BranchOffice("officename" +i, cities.get(randomIndex).getId(),
                    users.get(randomIndex).getId(), true, "email@e.com", 123456633, "Address", 0));
        }
        //Map<String, String> cityNames = cityManager.getCityNames(false);
        Map<String, String> userNames = userManager.getUserNames(false);
        List<BranchOffice> branchOffices = IteratorUtils.toList(branchOfficeManager.find(new JSONObject(), null).iterator());
        branchOffices.stream().forEach(office -> {
            //assertTrue(office.getAttributes().get(BranchOffice.CITY_NAME).toString()
            //        .equals(cityNames.get(office.getCityId())));
            assertTrue(office.getAttributes().get(BranchOffice.MANAGER_NAME).toString()
                    .equals(userNames.get(office.getManagerId())));
        });

    }
}