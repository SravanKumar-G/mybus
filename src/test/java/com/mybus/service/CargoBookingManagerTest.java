package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.CargoBooking;
import com.mybus.test.util.CargoBookingTestService;
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
public class CargoBookingManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Autowired
    private CargoBookingManager shipmentManager;

    @Before
    @After
    public void cleanup() {
        cargoBookingDAO.deleteAll();
    }

    public void testSave() throws Exception {

    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithValidations() throws Exception {
        CargoBooking shipment = new CargoBooking();
        shipmentManager.saveWithValidations(shipment);
    }

    @Test
    public void testSaveWithValidationsNoError() throws Exception {
        CargoBooking shipment = CargoBookingTestService.createNew();
        CargoBooking saved = shipmentManager.saveWithValidations(shipment);
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        Assert.assertEquals(1, shipments.size());
    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithNoDispatchDate() throws Exception {
        CargoBooking shipment = CargoBookingTestService.createNew();
        shipment.setDispatchDate(null);
        shipmentManager.saveWithValidations(shipment);
    }

}