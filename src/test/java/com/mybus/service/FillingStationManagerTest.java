package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.SupplierDAO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FillingStationManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SupplierDAO fillingStationDAO;

    @Test
    public void testFindAll() {


    }
}