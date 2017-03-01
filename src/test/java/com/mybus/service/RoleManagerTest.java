package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.RoleDAO;
import com.mybus.model.Role;
import static org.junit.Assert.*;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
public class RoleManagerTest extends AbstractControllerIntegrationTest{


    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private RoleManager roleManager;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        cleanup();
    }

    void cleanup(){
        roleDAO.deleteAll();
    }


    public Role createRole(){
        Set<String> menus = new HashSet<String>();
        menus.add("home");
        menus.add("User");
        Role role = new Role("test",menus);
        return roleDAO.save(role);
    }


    @Test
    public void testSaveRole() throws Exception {
        Role role = createRole();
        Role duplicateRole = createRole();
        roleDAO.save(role);
        assertNotNull(roleDAO.findOne(role.getId()));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role already exists with the same name");
        roleManager.saveRole(duplicateRole);
    }

    @Test
    public void testUpdateRole() throws Exception {
        Role role = createRole();
        Role role1 = roleManager.saveRole(new Role("test1"));
        ArrayList roles = (ArrayList) IteratorUtils.toList(roleDAO.findAll().iterator());
        assertEquals(2, roles.size());
        role1.setName("test");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role already exists with the same name");
        roleManager.saveRole(role1);
        assertEquals("test", role.getName());
    }

    @Test
    public void testDeleteRole() throws Exception {
        Role role = createRole();
        assertNotNull(role);
        assertNotNull(role.getId());
        roleDAO.delete(role);
        assertNull(roleDAO.findOne(role.getId()));
    }

    @Test
    public void getRole() throws Exception {
        Role role = createRole();
        assertNotNull(role);
        assertNotNull(role.getId());
        roleDAO.findOne(role.getId());
        assertNotNull(roleDAO.findOne(role.getId()));
    }

    @Test
    public void getRoleNames() throws Exception {
        for(int i=0; i<5; i++) {
            Role role = createRole();
            if(i%2 == 0){
                role.setActive(true);
            }
            roleDAO.save(role);
        }
        List<Role> roles = IteratorUtils.toList(roleManager.getRoleNames().iterator());
        assertEquals(3, roles.size());
        assertNull(roles.get(0).getMenus());
        assertNull(roles.get(1).getMenus());

    }
    @Test
    public void updateManagingRole(){
    	Role role = createRole();
    	Set<String> menus = role.getMenus(); 
    	menus.add("persons");
    	menus.add("config");
    	role.setMenus(menus);
    	Role roleTest = roleManager.updateManagingRoles(role);
    	assertNotNull(roleTest);
    	assertNotNull(roleTest.getMenus());
    	assertEquals(4, roleTest.getMenus().size());
    }
   
}