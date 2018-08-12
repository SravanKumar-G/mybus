package com.mybus.test.util;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.service.SessionManager;
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

    @Autowired
    private SessionManager sessionManager;
    public CargoBooking createNew(ShipmentSequence shipmentSequence ) {

        BranchOffice b1 = new BranchOffice("B1", "C1");
        b1.setOperatorId(sessionManager.getOperatorId());
        BranchOffice b2 = new BranchOffice("B2", "C2");
        b2.setOperatorId(sessionManager.getOperatorId());

        b1 = branchOfficeDAO.save(b1);
        b2 = branchOfficeDAO.save(b2);

        CargoBooking shipment = new CargoBooking();
        shipment.setFromEmail("email@e.com");
        shipment.setFromBranchId(b1.getId());
        shipment.setToBranchId(b2.getId());
        shipment.setCargoTransitStatus(CargoTransitStatus.ARRIVED);
        shipment.setShipmentType(shipmentSequence.getId());
        shipment.setFromContact(new Long(1234));
        shipment.setToContact(new Long(1234));
        shipment.setFromName("from");
        shipment.setToName("to");
        shipment.setTotalCharge(100);
        if(shipmentSequence.getShipmentCode().equals("F")){
            shipment.setPaymentType(PaymentStatus.FREE.toString());
        } else if(shipmentSequence.getShipmentCode().equals("TP")){
            shipment.setPaymentType(PaymentStatus.TOPAY.getKey());
        } else if(shipmentSequence.getShipmentCode().equals("P")){
            shipment.setPaymentType(PaymentStatus.PAID.toString());
        }
        shipment.setDispatchDate(new Date());
        return shipment;
    }
}
