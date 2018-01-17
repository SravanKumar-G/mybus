package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.ShipmentDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Shipment;
import com.mybus.model.ShipmentType;
import com.mybus.test.util.ShipmentTestService;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by srinikandula on 12/10/16.
 */
public class ShipmentManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ShipmentDAO shipmentDAO;

    @Autowired
    private ShipmentManager shipmentManager;

    @Before
    @After
    public void cleanup() {
        shipmentDAO.deleteAll();
    }

    public void testSave() throws Exception {

    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithValidations() throws Exception {
        Shipment shipment = new Shipment();
        shipmentManager.saveWithValidations(shipment);
    }

    @Test
    public void testSaveWithValidationsNoError() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        Shipment saved = shipmentManager.saveWithValidations(shipment);
        List<Shipment> shipments = IteratorUtils.toList(shipmentDAO.findAll().iterator());
        Assert.assertEquals(1, shipments.size());
    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithNoDispatchDate() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        shipment.setDispatchDate(null);
        shipmentManager.saveWithValidations(shipment);
    }

}