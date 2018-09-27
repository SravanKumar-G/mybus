package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.*;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.CargoBooking;
import com.mybus.model.OperatorAccount;
import com.mybus.model.User;
import com.mybus.model.Vehicle;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.test.util.CargoBookingTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by srinikandula on 12/10/16.
 */
public class CargoBookingManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Autowired
    private CargoBookingManager shipmentManager;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;
    @Autowired
    private CargoBookingTestService cargoBookingTestService;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private SMSNotificationDAO smsNotificationDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private VehicleDAO vehicleDAO;

    @Before
    @After
    public void cleanup() {
        vehicleDAO.deleteAll();
        cargoBookingDAO.deleteAll();
        branchOfficeDAO.deleteAll();
        shipmentSequenceDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        smsNotificationDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount.setSmsSenderName("SRIKRI");
        operatorAccount.setName("test");
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        User user = new User();
        user.setUserName("test");
        user = userDAO.save(user);
        sessionManager.setCurrentUser(user);
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
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        CargoBooking saved = shipmentManager.saveWithValidations(shipment);
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithNoDispatchDate() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        shipment.setDispatchDate(null);
        shipmentManager.saveWithValidations(shipment);
    }

    @Test
    public void testFindContactInfo() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        shipment.setOperatorId(sessionManager.getOperatorId());
        shipment = shipmentManager.saveWithValidations(shipment);
        JSONObject jsonObject = shipmentManager.findContactInfo("from", shipment.getFromContact());
        assertEquals(jsonObject.get("name"), "from");
        assertEquals(jsonObject.get("email"), "email@e.com");
        jsonObject = shipmentManager.findContactInfo("to", shipment.getToContact());
        assertEquals(jsonObject.get("name"), "to");
        assertEquals(jsonObject.get("email"), "to@e.com");
    }

    @Test
    public void testAssignVehicle() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        shipment.setOperatorId(sessionManager.getOperatorId());
        CargoBooking shipment1 = cargoBookingTestService.createNew(shipmentSequence);
        shipment1.setOperatorId(sessionManager.getOperatorId());
        shipment = shipmentManager.saveWithValidations(shipment);
        shipment1 = shipmentManager.saveWithValidations(shipment1);
        CargoBooking shipment2 = cargoBookingTestService.createNew(shipmentSequence);
        shipment2.setOperatorId(sessionManager.getOperatorId());
        shipment2 = shipmentManager.saveWithValidations(shipment2);
        List<String> ids = new ArrayList<>();
        ids.add(shipment.getId());
        ids.add(shipment1.getId());

        Vehicle vehicle = new Vehicle();
        vehicle.setRegNo("AP27TU1234");
        vehicle = vehicleDAO.save(vehicle);
        assertTrue(shipmentManager.assignVehicle(vehicle.getId(), ids));
        shipment = cargoBookingDAO.findOne(shipment.getId());
        shipment1 = cargoBookingDAO.findOne(shipment1.getId());
        shipment2 = cargoBookingDAO.findOne(shipment2.getId());

        assertEquals(shipment.getVehicleId(), vehicle.getId());
        assertEquals(shipment1.getVehicleId(), vehicle.getId());
        assertNull(shipment2.getVehicleId());
    }

}