package com.mybus.service;

import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
public class PaymentManager {
    private static final Logger logger = LoggerFactory.getLogger(PaymentManager.class);

    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private BranchOfficeMongoDAO branchOfficeMongoDAO;

    public Payment updatePayment(Payment payment) {
        if(payment.getStatus() != null && payment.getStatus().equals(Payment.STATUS_APPROVED)){
            if(payment.getType().equals(PaymentType.EXPENSE)){
                branchOfficeMongoDAO.updateCashBalance(payment.getBranchOfficeId(), (0-payment.getAmount()));
            } else if(payment.getType().equals(PaymentType.INCOME)){
                branchOfficeMongoDAO.updateCashBalance(payment.getBranchOfficeId(), payment.getAmount());
            }
        }
        return paymentDAO.save(payment);
    }

    public void delete(String paymentId) {
        Payment payment = paymentDAO.findOne(paymentId);
        if(payment.getStatus() != null) {
            throw new BadRequestException("Payment can not be deleted");
        }
        paymentDAO.delete(payment);
    }
}
