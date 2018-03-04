package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private ServiceUtils serviceUtils;

    @Autowired
    private UserManager userManager;
    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;


    public Payment updatePayment(Payment payment) {
        if(payment.getStatus() != null && (payment.getStatus().equals(Payment.STATUS_APPROVED) ||
                payment.getStatus().equals(Payment.STATUS_AUTO))){
            updateUserBalance(payment);
        }
        return paymentMongoDAO.save(payment);
    }

    private void updateUserBalance(Payment payment) {
        User currentUser = null;
        if(payment.getCreatedBy() != null) {
            currentUser = userManager.findOne(payment.getCreatedBy());
        }
        if(payment.getSubmittedBy() != null) {
            currentUser = userManager.findOne(payment.getSubmittedBy());
        }
        if(currentUser == null){
            currentUser = sessionManager.getCurrentUser();
        }
        if(currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }
        if(!payment.getStatus().equals(Payment.STATUS_AUTO)){
            currentUser = userManager.findOne(payment.getCreatedBy());
        }
        if(payment.getType().equals(PaymentType.EXPENSE)){
            userMongoDAO.updateCashBalance(currentUser.getId(), (0-payment.getAmount()));
        } else if(payment.getType().equals(PaymentType.INCOME)){
            userMongoDAO.updateCashBalance(currentUser.getId(), payment.getAmount());
        }
    }

    /**
     * Create payment for given office and update office's cash balance
     * @param booking
     * @return
     */
    public Payment createPayment(Booking booking) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setAmount(booking.getNetAmt());
        payment.setDate(booking.getJourneyDate());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setBookingId(booking.getId());
        payment.setSubmittedBy(currentUser.getId());
        payment.setDescription(Payment.BOOKING_DUE_PAYMENT + " "+booking.getBookedBy()+" : " + booking.getTicketNo());
        payment.setType(PaymentType.INCOME);
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDuePaidOn(new Date());
        return updatePayment(payment);
    }

    public Payment createPayment(ServiceForm serviceForm, boolean deleteForm) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setAmount(serviceForm.getNetCashIncome());
        payment.setServiceFormId(serviceForm.getId());

        //need to set this for updating the balance for verified forms
        payment.setSubmittedBy(serviceForm.getSubmittedBy());
        if(deleteForm){
            payment.setType(PaymentType.EXPENSE);
            payment.setDescription("Service form refresh");
        } else {
            payment.setType(PaymentType.INCOME);
            payment.setDescription("Service form: "+ serviceForm.getServiceName());
        }
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDate(serviceForm.getJDate());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        return updatePayment(payment);
    }

    public void delete(String paymentId) {
        Payment payment = paymentDAO.findOne(paymentId);
        if(payment.getStatus() != null) {
            throw new BadRequestException("Payment can not be deleted");
        }
        paymentDAO.delete(payment);
    }

    public Page<Payment> findPayments(JSONObject query, Pageable pageable) {
        List<Payment> payments = IteratorUtils.toList(paymentMongoDAO.find(query, pageable).iterator());
        long count =  paymentMongoDAO.count(query);
        serviceUtils.fillInUserNames(payments);
        Page<Payment> page = new PageImpl<>(payments, pageable, count);
        return page;
    }

    public Page<Payment> findPendingPayments(Pageable pageable) {
        Page<Payment> page = paymentMongoDAO.findPendingPayments(pageable);
        serviceUtils.fillInUserNames(page.getContent());
        return page;
    }
    public Page<Payment> findNonPendingPayments(Pageable pageable) {
        Page<Payment> page = paymentMongoDAO.findNonPendingPayments(pageable);
        serviceUtils.fillInUserNames(page.getContent());
        return page;
    }


    public Payment findOne(String id) {
        Payment payment = paymentDAO.findOne(id);
        if(payment == null) {
            throw new BadRequestException("No Payment found");
        }
        serviceUtils.fillInUserNames(payment);
        return payment;
    }

    public Page<Payment> findPaymentsByDate(String date, Pageable pageable) throws IOException {
        Page<Payment> payments = paymentMongoDAO.findPaymentsByDate(date, pageable);
        serviceUtils.fillInUserNames(payments.getContent(), ServiceReport.SUBMITTED_BY);
        return payments;
    }

    public List<Payment> search(JSONObject query, Pageable pageable) throws ParseException {
        return paymentMongoDAO.search(query, pageable);
    }

    public List<Payment> approveOrRejectExpenses(List<String> ids, Boolean approve) {
        List<Payment> payments = new ArrayList<>();
        User currentUser = sessionManager.getCurrentUser();
        ids.stream().forEach(id -> {
            Payment payment = paymentDAO.findOne(id);
            if(payment.getStatus() != null){
                throw new BadRequestException("payment has invalid status");
            }
            if(approve){
                payment.setStatus(Payment.STATUS_APPROVED);
                payment.setReviewedBy(currentUser.getId());
                payment.setReviewdOn(new Date());
                payment = updatePayment(payment);
            } else {
                payment.setStatus(Payment.STATUS_REJECTED);
                payment.setReviewedBy(currentUser.getId());
                payment.setReviewdOn(new Date());
                payment = updatePayment(payment);
            }
            payments.add(payment);
        });
        return payments;
    }
}
