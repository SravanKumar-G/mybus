package com.mybus.test.util;

import com.mybus.model.PaymentStatus;
import com.mybus.model.Shipment;
import com.mybus.model.ShipmentStatus;
import com.mybus.model.ShipmentType;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by srinikandula on 12/11/16.
 */
public class ShipmentTestService {

    public static Shipment createNew() {
        Shipment shipment = new Shipment();
        shipment.setFromEmail("email@e.com");
        shipment.setFromBranchId("1234");
        shipment.setToBranchId("1234");
        shipment.setShipmentStatus(ShipmentStatus.ARRIVED);
        shipment.setShipmentType(ShipmentType.FREE);
        shipment.setFromContact(1234);
        shipment.setToContact(1234);
        shipment.setNoOfPackages(2);
        shipment.setFromName("from");
        shipment.setToName("to");
        shipment.setTotalCharge(100);
        shipment.setDispatchDate(new Date());
        shipment.setContents("Something");
        return shipment;
    }
}
