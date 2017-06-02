package com.mybus.service;

import com.mybus.dao.CashTransferDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.model.CashTransfer;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by srinikandula on 3/19/17.
 */
@Service
public class CashTransferManager {

    @Autowired
    private CashTransferDAO cashTransferDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private PaymentDAO paymentDAO;


    /**
     *
     * @param cashTransfer
     * @return
     */
    public CashTransfer updateCashTransfer(CashTransfer cashTransfer){
        if(cashTransfer.getStatus() != null && cashTransfer.getStatus().equals(CashTransfer.STATUS_APPROVED)) {
            Payment expense = new Payment();
            expense.setBranchOfficeId(cashTransfer.getFromOfficeId());
            expense.setAmount(cashTransfer.getAmount());
            expense.setType(PaymentType.EXPENSE);
            expense.setStatus(Payment.STATUS_AUTO);
            expense.setDescription(Payment.CASH_TRANSFER);
            expense.setDate(new Date());
            paymentDAO.save(expense);

            Payment income = new Payment();
            income.setBranchOfficeId(cashTransfer.getToOfficeId());
            income.setAmount(cashTransfer.getAmount());
            income.setType(PaymentType.INCOME);
            income.setStatus(Payment.STATUS_AUTO);
            income.setDescription(Payment.CASH_TRANSFER);
            income.setDate(new Date());
            paymentDAO.save(income);
            userMongoDAO.updateCashBalance(cashTransfer.getFromUserId(), (0-cashTransfer.getAmount()));
            userMongoDAO.updateCashBalance(cashTransfer.getToUserId(), cashTransfer.getAmount());
        }
        return cashTransferDAO.save(cashTransfer);
    }

    public CashTransfer get(String id) {
        return cashTransferDAO.findOne(id);
    }

    public CashTransfer save(CashTransfer cashTransfer){
        return cashTransferDAO.save(cashTransfer);
    }
    public CashTransfer update(CashTransfer cashTransfer){
        return cashTransferDAO.save(cashTransfer);
    }
    public void delete(String id){
        cashTransferDAO.delete(id);
    }

    public CashTransfer findOne(String id) {
        return cashTransferDAO.findOne(id);
    }
}
