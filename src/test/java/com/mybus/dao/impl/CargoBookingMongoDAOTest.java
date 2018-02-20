package com.mybus.dao.impl;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.model.CargoBooking;
import com.mybus.test.util.CargoBookingTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Created by srinikandula on 12/11/16.
 */
public class CargoBookingMongoDAOTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Before
    @After
    public void cleanup() {
        cargoBookingDAO.deleteAll();
    }

    @Test
    public void testFindShipments() throws Exception {
        for(int i=0; i<5; i++) {
            CargoBooking shipment = CargoBookingTestService.createNew();
            shipment = cargoBookingDAO.save(shipment);
            System.out.println(shipment.getId());
        }
        JSONObject query = new JSONObject();
        query.put("fromBranchId", "1234");
        List<CargoBooking> shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(5, shipments.size());
        query.put("fromBranchId", "12345");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(0, shipments.size());

        //test with toCityId
        query.remove("fromBranchId");
        query.put("toBranchId", "1234");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(5, shipments.size());
        query.put("toBranchId", "12345");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(0, shipments.size());

        //test my email
        query.remove("toBranchId");
        query.put("fromEmail", "email@e.com");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(5, shipments.size());

        //change email and test
        CargoBooking shipment = shipments.get(0);
        shipment.setFromEmail("srini@email.com");
        cargoBookingDAO.save(shipment);
        query.put("fromEmail", "srini@email.com");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(1, shipments.size());

        //test querying only email field
        String fields[] = {"fromEmail"};
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, fields, query, null).iterator());
        assertEquals(1, shipments.size());
        shipment = shipments.get(0);
        assertEquals("srini@email.com", shipment.getFromEmail());
        assertNull(shipment.getFromBranchId());
        assertNull(shipment.getToBranchId());
    }
}