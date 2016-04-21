package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.UserDAO;
import com.mybus.model.CommissionType;
import com.mybus.model.Route;
import com.mybus.model.User;
import com.mybus.model.UserType;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Iterator;
import java.util.List;

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

    public User createUser(){
        User user = new User("fname", "lname", "uname", "pwd", "e@email.com", "1234567", "add1", "add2",
                "city", "state", UserType.ADMIN,"plan3");
        return userDAO.save(user);
    }

    @Test
    public void testSaveUser() throws Exception {
        User user = createUser();
        User duplicate = new User("fname", "lname", "uname", "pwd", "e@email.com", "1234567", "add1", "add2",
                "city", "state", UserType.ADMIN, "plan3");
        userDAO.save(user);
        Assert.assertNotNull(userDAO.findOne(user.getId()));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("A user already exists with username");
        userManager.saveUser(duplicate);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = createUser();
        User duplicate = new User("fname", "lname", "unamenew", "pwd", "e@email.com", "1234567", "add1", "add2",
                "city", "state", UserType.USER, "plan2");
        userDAO.save(user);
        userManager.saveUser(duplicate);
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(2, userList.size());
        duplicate.setUserName("uname");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("A user already exists with username");
        userManager.updateUser(duplicate);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = createUser();
        userDAO.save(user);
        user = userDAO.findOne(user.getId());
        Assert.assertNotNull(user);
        userDAO.delete(user);
        Assert.assertNull(userDAO.findOne(user.getId()));
    }

    @Test
    public void testGetUser() throws Exception{
        User user = createUser();
        userDAO.save(user);
        user = userDAO.findOne(user.getId());
        Assert.assertNotNull(user);
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1,userList.size());
    }
}