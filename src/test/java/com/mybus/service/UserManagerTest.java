package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.UserDAO;
import com.mybus.model.*;
import com.mybus.test.util.UserTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by skandula on 2/13/16.
 */
public class UserManagerTest  extends AbstractControllerIntegrationTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserManager userManager;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }



    @Test
    public void testSaveUser() throws Exception {
        User user = UserTestService.createNew();
        User duplicate = new User("fname", "lname", "uname", "pwd", "e@email.com", 1234567, "add1", "add2",
                "city", "state", UserType.ADMIN, "plan3");
        userDAO.save(user);
        assertNotNull(userDAO.findOne(user.getId()));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("A user already exists with username");
        userManager.saveUser(duplicate);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = UserTestService.createNew();
        User duplicate = new User("fname", "lname", "unamenew", "pwd", "e@email.com", 1234567, "add1", "add2",
                "city", "state", UserType.USER, "plan2");
        userDAO.save(user);
        userManager.saveUser(duplicate);
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        assertEquals(2, userList.size());
        duplicate.setUserName("uname");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("A user already exists with username");
        userManager.updateUser(duplicate);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = UserTestService.createNew();
        userDAO.save(user);
        user = userDAO.findOne(user.getId());
        assertNotNull(user);
        userDAO.delete(user);
        assertNull(userDAO.findOne(user.getId()));
    }

    @Test
    public void testGetUser() throws Exception{
        User user = UserTestService.createNew();
        userDAO.save(user);
        user = userDAO.findOne(user.getId());
        assertNotNull(user);
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        assertEquals(1, userList.size());
    }

    @Test
    public void testGetUserNames() {
        for(int i =0; i<10; i++) {
            User user = UserTestService.createNew();
            if(i%2 ==0) {
                user.setActive(true);
            }
            user.setLastName(user.getLastName()+i);
            user.setEmail(i+ user.getEmail());
            user = userDAO.save(user);
        }
        Map<String, String> userNames = userManager.getUserNames(false);
        assertEquals(5, userNames.values().size());
        userNames.values().parallelStream().forEach(name -> {
            assertTrue(name.equals("fname, lname0") ||
                    name.equals("fname, lname2")||
                    name.equals("fname, lname4")||
                    name.equals("fname, lname6")||
                    name.equals("fname, lname8"));
        });
    }
}