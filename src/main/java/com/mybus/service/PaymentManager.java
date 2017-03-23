package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
public class PaymentManager {
    private static final Logger logger = LoggerFactory.getLogger(PaymentManager.class);

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeMongoDAO branchOfficeMongoDAO;

    @Autowired
    private UserManager userManager;
    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    public Payment updatePayment(Payment payment) {
        logger.debug("updating balance for office:" + payment.getBranchOfficeId() + " type:"+payment.getType());
        if(payment.getStatus() != null && (payment.getStatus().equals(Payment.STATUS_APPROVED) ||
                payment.getStatus().equals(Payment.STATUS_AUTO))){
            if(payment.getType().equals(PaymentType.EXPENSE)){
                branchOfficeMongoDAO.updateCashBalance(payment.getBranchOfficeId(), (0-payment.getAmount()));
            } else if(payment.getType().equals(PaymentType.INCOME)){
                branchOfficeMongoDAO.updateCashBalance(payment.getBranchOfficeId(), payment.getAmount());
            }
        }
        return paymentDAO.save(payment);
    }

    /**
     * Create payment for given office and update office's cash balance
     * @param booking
     * @return
     */
    public Payment createPayment(Booking booking) {
        Agent agent = agentDAO.findByUsername(booking.getBookedBy());
        Payment payment = new Payment();
        payment.setBranchOfficeId(agent.getBranchOfficeId());
        payment.setAmount(booking.getNetAmt());
        payment.setDate(booking.getJourneyDate());
        payment.getAttributes().put("BookingId", booking.getId());
        payment.setDescription(Payment.BOOKING_DUE_PAYMENT);
        payment.setType(PaymentType.INCOME);
        payment.setStatus(Payment.STATUS_AUTO);
        return updatePayment(payment);
    }

    public Payment createPayment(ServiceForm serviceForm, boolean deleteForm) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setAmount(serviceForm.getNetCashIncome());
        if(deleteForm){
            payment.setType(PaymentType.EXPENSE);
        } else {
            payment.setType(PaymentType.INCOME);
        }
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDescription("Service form refresh");
        payment.setDate(serviceForm.getJDate());
        return updatePayment(payment);
    }

    public void delete(String paymentId) {
        Payment payment = paymentDAO.findOne(paymentId);
        if(payment.getStatus() != null) {
            throw new BadRequestException("Payment can not be deleted");
        }
        paymentDAO.delete(payment);
    }
    public Iterable<Payment> findPayments(JSONObject query, Pageable pageable) {
        Iterable<Payment> payments = paymentMongoDAO.find(query, pageable);
        Map<String, String> userNames = userManager.getUserNames(true);
        payments.forEach(payment -> {
            payment.getAttributes().put("createdBy", userNames.get(payment.getCreatedBy()));
        });
        return payments;
    }
}
