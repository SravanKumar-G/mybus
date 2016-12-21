package com.mybus.test.util;

import com.mybus.model.PaymentStatus;
import com.mybus.model.Shipment;
import com.mybus.model.ShipmentStatus;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by srinikandula on 12/11/16.
 */
public class ShipmentTestService {

    public static Shipment createNew() {
        Shipment shipment = new Shipment();
        shipment.setEmail("email@e.com");
        shipment.setFromCityId("1234");
        shipment.setToCityId("1234");
        shipment.setPaymentStatus(PaymentStatus.PAID);
        shipment.setShipmentStatus(ShipmentStatus.ARRIVED);
        shipment.setFromContact(1234);
        shipment.setToContact(1234);
        shipment.setNoOfPackages(2);
        shipment.setFromNameAddress("from");
        shipment.setToNameAddress("to");
        shipment.setTotalCharge(100);
        shipment.setDispatchDate(new Date());
        shipment.setContents("Something");
        return shipment;
    }
}
