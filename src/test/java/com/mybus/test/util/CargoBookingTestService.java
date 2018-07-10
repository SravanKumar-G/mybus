package com.mybus.test.util;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by srinikandula on 12/11/16.
 */
@Service
public class CargoBookingTestService {

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    public CargoBooking createNew(ShipmentSequence shipmentSequence ) {
        BranchOffice b1 = branchOfficeDAO.save(new BranchOffice("B1", "C1"));
        BranchOffice b2 = branchOfficeDAO.save(new BranchOffice("B2", "C2"));

        CargoBooking shipment = new CargoBooking();
        shipment.setFromEmail("email@e.com");
        shipment.setFromBranchId(b1.getId());
        shipment.setToBranchId(b2.getId());
        shipment.setShipmentStatus(ShipmentStatus.ARRIVED);
        shipment.setShipmentType(shipmentSequence.getId());
        shipment.setFromContact(new Long(1234));
        shipment.setToContact(new Long(1234));
        shipment.setFromName("from");
        shipment.setToName("to");
        shipment.setTotalCharge(100);
        if(shipmentSequence.getShipmentCode().equals("F")){
            shipment.setPaymentStatus(PaymentStatus.FREE);
        } else if(shipmentSequence.getShipmentCode().equals("TP")){
            shipment.setPaymentStatus(PaymentStatus.TOPAY);
        } else if(shipmentSequence.getShipmentCode().equals("P")){
            shipment.setPaymentStatus(PaymentStatus.PAID);
        }
        shipment.setDispatchDate(new Date());
        return shipment;
    }
}
