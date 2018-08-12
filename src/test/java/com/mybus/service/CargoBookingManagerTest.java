package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.*;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.CargoBooking;
import com.mybus.model.OperatorAccount;
import com.mybus.model.PaymentStatus;
import com.mybus.model.User;
import com.mybus.model.cargo.ShipmentSequence;
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

    @Before
    @After
    public void cleanup() {
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
        Assert.assertEquals(1, shipments.size());
    }

    @Test(expected = BadRequestException.class)
    public void testSaveWithNoDispatchDate() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        shipment.setDispatchDate(null);
        shipmentManager.saveWithValidations(shipment);
    }

}