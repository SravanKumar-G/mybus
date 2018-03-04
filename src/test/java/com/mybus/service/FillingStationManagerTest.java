package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.FillingStationDAO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FillingStationManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private FillingStationDAO fillingStationDAO;

    @Test
    public void testFindAll() {


    }
}