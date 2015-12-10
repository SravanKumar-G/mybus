package com.mybus.controller;

import com.mybus.dao.CityDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by skandula on 12/9/15.
 */
public class CityControllerTest extends AbstractControllerIntegrationTest{

    @Autowired
    private CityDAO cityDAO;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        cityDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testCreateCity() throws Exception {

    }
}