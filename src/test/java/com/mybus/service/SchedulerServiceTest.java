package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.Vehicle;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

public class SchedulerServiceTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private VehicleDAO vehicleDAO;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        vehicleDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testSendExpiredNotifications() {
       DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(300);
        for( int i=0; i<10; i++) {
            Vehicle v = new Vehicle();
            v.setRegNo("AP27"+i);
            if(i%2 == 0){
                v.setAuthExpiry(dateTime);
                v.setFitnessExpiry(dateTime);
                v.setPermitExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else if(i%3 == 0) {
                v.setPermitExpiry(dateTime);
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else if(i%5 == 0){
                v.setInsuranceExpiry(dateTime);
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setPermitExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else {
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setPermitExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            }
            vehicleDAO.save(v);
        }
        schedulerService.checkExpiryDates();
    }
}