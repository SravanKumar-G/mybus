package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.impl.PaymentGatewayMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HARIPRASADREDDYGURAM on 5/8/2016.
 */
@Service
public class PaymentGatewayManager {
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayManager.class);

    @Autowired
    private PaymentGatewayDAO payGWDAO;

    @Autowired
    private PaymentGatewayMongoDAO payGWMonDAO;
    public PaymentGateway savePaymentGateway(PaymentGateway payGW){
        //Preconditions.checkNotNull(payGW, "The city can not be null");
        Preconditions.checkNotNull(payGW.getName(), "The city name can not be null");
       // Preconditions.checkNotNull(payGW.getPgAccountID(), "The city State can not be null");
        return payGWDAO.save(payGW);
    }

    public boolean updatePaymentGateWay(PaymentGateway payGW) {
        Preconditions.checkNotNull(payGW.getName(), "The payment gateway name can not be null");
        PaymentGateway matchingPG = payGWDAO.findByName(payGW.getName());
        if(matchingPG != null && !payGW.getId().equals(matchingPG.getId())) {
            throw new BadRequestException("A payment gateway already exists with same name");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Updating city:[{}]" + payGW);
        }
        return payGWMonDAO.updatePaymentGateWay(payGW);
    }
    public boolean deletePaymentGateway(String id) {
        Preconditions.checkNotNull(id, "The paymentGateway id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting paymentGateway:[{}]" + id);
        }
        if (payGWDAO.findOne(id) != null) {
            payGWDAO.delete(id);
        } else {
            throw new RuntimeException("Unknown city id");
        }
        return true;
    }

}
