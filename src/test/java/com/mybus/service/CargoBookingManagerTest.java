package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.*;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.dto.BranchDeliverySummary;
import com.mybus.dto.BranchwiseCargoBookingSummary;
import com.mybus.dto.UserCargoBookingsSummary;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.test.util.CargoBookingTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private User currentUser;
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
        user.setFirstName("test");
        user.setLastName("last");

        user.setOperatorId(operatorAccount.getId());
        currentUser = userDAO.save(user);
        sessionManager.setCurrentUser(currentUser);
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
        shipment = cargoBookingDAO.findById(shipment.getId()).get();
        shipment1 = cargoBookingDAO.findById(shipment1.getId()).get();
        shipment2 = cargoBookingDAO.findById(shipment2.getId()).get();

        assertEquals(shipment.getVehicleId(), vehicle.getId());
        assertEquals(shipment1.getVehicleId(), vehicle.getId());
        assertNull(shipment2.getVehicleId());
    }

    @Test
    public void testGetBranchwiseBookingsSummary() throws Exception{
        BranchOffice b1 = new BranchOffice("B1", "C1");
        b1.setOperatorId(sessionManager.getOperatorId());
        BranchOffice b2 = new BranchOffice("B2", "C2");
        b2.setOperatorId(sessionManager.getOperatorId());
        b1 = branchOfficeDAO.save(b1);
        b2 = branchOfficeDAO.save(b2);
        ShipmentSequence paid = shipmentSequenceDAO.save(new ShipmentSequence("P", "Paid"));
        ShipmentSequence free = shipmentSequenceDAO.save(new ShipmentSequence("TP", "ToPay"));
        for(int i=0; i< 10; i++){
            CargoBooking shipment = new CargoBooking();
            if(i%2 == 0){
                shipment.setPaymentType(PaymentStatus.TOPAY.getKey());
            } else {
                shipment.setPaymentType(PaymentStatus.PAID.getKey());
            }
            shipment.setFromEmail("email@e.com");
            shipment.setToEmail("to@e.com");
            shipment.setFromBranchId(b1.getId());
            shipment.setToBranchId(b2.getId());
            shipment.setCargoTransitStatus(CargoTransitStatus.ARRIVED);
            shipment.setFromContact(new Long(1234));
            shipment.setToContact(new Long(12345678));
            shipment.setFromName("from");
            shipment.setToName("to");
            shipment.setTotalCharge(100);
            shipment.setDispatchDate(new Date());
            shipmentManager.saveWithValidations(shipment);
        }
        BranchwiseCargoBookingSummary summary = shipmentManager.getBranchwiseCargoBookingSummary(b1.getId(),new Date(), new Date());
        assertEquals(summary.getBranchCargoBookings().size() ,2);
    }

    @Test
    public void testGetAllBranchBookingsSummary() throws Exception{
        BranchOffice b1 = new BranchOffice("B1", "C1");
        b1.setOperatorId(sessionManager.getOperatorId());
        BranchOffice b2 = new BranchOffice("B2", "C2");
        b2.setOperatorId(sessionManager.getOperatorId());
        b1 = branchOfficeDAO.save(b1);
        b2 = branchOfficeDAO.save(b2);
        ShipmentSequence paid = shipmentSequenceDAO.save(new ShipmentSequence("P", "Paid"));
        ShipmentSequence free = shipmentSequenceDAO.save(new ShipmentSequence("TP", "ToPay"));
        for(int i=0; i< 20; i++){
            CargoBooking shipment = new CargoBooking();
            if(i%3 == 0){
                shipment.setPaymentType(PaymentStatus.TOPAY.getKey());
                shipment.setFromBranchId(b1.getId());
                shipment.setToBranchId(b2.getId());
            } else if(i%2 == 0){
                shipment.setPaymentType(PaymentStatus.PAID.getKey());
                shipment.setFromBranchId(b1.getId());
                shipment.setToBranchId(b2.getId());
            }else {
                shipment.setPaymentType(PaymentStatus.PAID.getKey());
                shipment.setFromBranchId(b2.getId());
                shipment.setToBranchId(b1.getId());
            }
            shipment.setFromEmail("email@e.com");
            shipment.setToEmail("to@e.com");
            shipment.setCargoTransitStatus(CargoTransitStatus.ARRIVED);
            shipment.setFromContact(new Long(1234));
            shipment.setToContact(new Long(12345678));
            shipment.setFromName("from");
            shipment.setToName("to");
            shipment.setTotalCharge(100);
            shipment.setDispatchDate(new Date());
            shipmentManager.saveWithValidations(shipment);
        }
        BranchwiseCargoBookingSummary summary = shipmentManager.getAllBranchCargoBookingSummary(new Date(), new Date());
        assertEquals(summary.getBranchCargoBookings().size() ,3);
        JSONObject query = new JSONObject();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, -1);
        DateFormat df = new SimpleDateFormat(ServiceConstants.df);
        query.put("startDate", df.format(calendar.getTime()));
        query.put("endDate", df.format(new Date()));
        summary = shipmentManager.getBranchSummary(query);
        assertEquals(summary.getBranchCargoBookings().size() ,2);
        assertEquals(summary.getUserCargoBookingsSummaries().size() ,1);
        UserCargoBookingsSummary userCargoBookingsSummary = summary.getUserCargoBookingsSummaries().get(0);
        assertEquals(userCargoBookingsSummary.getPaidBookingsCount(), 13);
        assertEquals(userCargoBookingsSummary.getPaidBookingsTotal(), 1300,0.0);
        assertEquals(userCargoBookingsSummary.getTopayBookingsCount(), 7);
        assertEquals(userCargoBookingsSummary.getTopayBookingsTotal(), 700, 0.0);
    }

    @Test
    public void testUnloadBookings(){
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
        CargoBooking saved = shipmentManager.saveWithValidations(shipment);
        List<String> ids = new ArrayList<>();
        ids.add(saved.getId());
        shipmentManager.unloadBookings(ids);
    }

    @Test
    public void testFindDeliveredToPayShipmentTotal() throws ParseException {
        BranchOffice b1 = new BranchOffice("B1", "C1");
        b1.setOperatorId(sessionManager.getOperatorId());
        BranchOffice b2 = new BranchOffice("B2", "C2");
        b2.setOperatorId(sessionManager.getOperatorId());
        b1 = branchOfficeDAO.save(b1);
        b2 = branchOfficeDAO.save(b2);
        currentUser.setBranchOfficeId(b2.getId());
        userDAO.save(currentUser);
        ShipmentSequence paid = shipmentSequenceDAO.save(new ShipmentSequence("P", "Paid"));
        ShipmentSequence free = shipmentSequenceDAO.save(new ShipmentSequence("TP", "ToPay"));
        List<CargoBooking> cargoBookings = new ArrayList<>();
        for(int i=0; i< 20; i++){
            CargoBooking shipment = new CargoBooking();
            if(i%2 == 0){
                shipment.setPaymentType(PaymentStatus.TOPAY.getKey());
                shipment.setFromBranchId(b1.getId());
                shipment.setToBranchId(b2.getId());
            } else {
                shipment.setPaymentType(PaymentStatus.PAID.getKey());
                shipment.setFromBranchId(b1.getId());
                shipment.setToBranchId(b2.getId());
            }
            shipment.setCargoTransitStatus(CargoTransitStatus.ARRIVED);
            shipment.setFromContact(new Long(1234));
            shipment.setToContact(new Long(12345678));
            shipment.setFromName("from");
            shipment.setToName("to");
            shipment.setTotalCharge(100);
            shipment.setDispatchDate(new Date());
            cargoBookings.add(shipmentManager.saveWithValidations(shipment));
        }
        for(int i=0; i< 20; i++){
            if(i%4 == 0){
                CargoBooking shipment = cargoBookings.get(i);
                shipmentManager.deliverCargoBooking(shipment.getId(), "Notes ");
            }
        }
        BranchDeliverySummary deliverySummary = shipmentManager.findDeliveryShipmentsTotalByBranchUsers(b2.getId(), PaymentStatus.TOPAY, new Date(), new Date());
        assertEquals(deliverySummary.getTotal(), 500, 0.0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, -2);
    }

}