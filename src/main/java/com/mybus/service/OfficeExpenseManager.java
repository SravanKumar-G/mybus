package com.mybus.service;

import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.impl.OfficeExpenseMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.ApproveStatus;
import com.mybus.model.OfficeExpense;
import com.mybus.model.Payment;
import com.mybus.model.User;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by srinikandula on 12/12/16.
 */

@Service
public class OfficeExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(OfficeExpenseManager.class);

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;

    @Autowired
    private OfficeExpenseMongoDAO officeExpenseMongoDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceUtils serviceUtils;

    public OfficeExpense save(OfficeExpense officeExpense) {
        officeExpense.setOperatorId(sessionManager.getOperatorId());
        return officeExpenseMongoDAO.save(officeExpense);
    }

    public OfficeExpense updateOfficeExpense(OfficeExpense officeExpense) {
        logger.debug("updating balance for office:" + officeExpense.getBranchOfficeId());
        if(officeExpense.getStatus() != null && (officeExpense.getStatus().equals(Payment.STATUS_APPROVED))){
            userMongoDAO.updateCashBalance(officeExpense.getCreatedBy(), (0-officeExpense.getAmount()));
        }
        return officeExpenseDAO.save(officeExpense);
    }

    /**
     * Approve or reject expenses
     * @param ids
     * @param approve
     * @return
     */
    public List<OfficeExpense> approveOrRejectExpenses(List<String> ids, boolean approve) {
        List<OfficeExpense> officeExpenses = new ArrayList<>();
        User currentUser = sessionManager.getCurrentUser();
        ids.stream().forEach(id -> {
            OfficeExpense officeExpense = officeExpenseDAO.findOne(id);
            if(officeExpense.getStatus() != null){
                throw new BadRequestException("OfficeExpense has invalid status");
            }
            if(approve){
                if(userMongoDAO.updateCashBalance(officeExpense.getCreatedBy(), (0-officeExpense.getAmount()))){
                    officeExpense.setStatus(ApproveStatus.Approved);
                    officeExpense.setReviewedBy(currentUser.getId());
                    officeExpense.setApprovedOn(new Date());
                    officeExpenseDAO.save(officeExpense);
                } else {
                    throw new BadRequestException("Failed to approve Office expense");
                }
            } else {
                officeExpense.setStatus(ApproveStatus.Rejected);
                officeExpense.setReviewedBy(currentUser.getId());
                officeExpense.setApprovedOn(new Date());
                officeExpenseDAO.save(officeExpense);
            }
            officeExpenses.add(officeExpense);
        });
        return officeExpenses;
    }

    public void delete(String id) {
        OfficeExpense officeExpense = officeExpenseDAO.findOne(id);
        if(officeExpense != null && officeExpense.getStatus() != null) {
            throw new BadRequestException("officeExpense can not be deleted");
        }
        officeExpenseDAO.delete(officeExpense);
    }

    /**
     * Load office expenses with user names
     * @param officeExpenses
     */
    private void loadUserNames(List<OfficeExpense> officeExpenses) {
        officeExpenses.forEach(officeExpense -> {
            serviceUtils.fillInUserNames(officeExpense);
        });
    }


    public long findCount(boolean pending) {
        return officeExpenseMongoDAO.count(pending);
    }
    public Page<OfficeExpense> findPendingOfficeExpenses(Pageable pageable) {
        Page<OfficeExpense> page = officeExpenseMongoDAO.findPendingExpenses(pageable);
        loadUserNames(page.getContent());
        return page;
    }
    public Page<OfficeExpense> findNonPendingOfficeExpenses(Pageable pageable) {
        Page<OfficeExpense> page = officeExpenseMongoDAO.findNonPendingExpenses(pageable);
        loadUserNames(page.getContent());
        return page;
    }

    public OfficeExpense findOne(String id) {
        OfficeExpense payment = officeExpenseDAO.findOne(id);
        if(payment == null) {
            throw new BadRequestException("No Payment found");
        }
        serviceUtils.fillInUserNames( payment);
        return payment;
    }

    public Page<OfficeExpense> findOfficeExpenseByDate(String date, Pageable pageable) {
        Page<OfficeExpense> officeExpenses = officeExpenseMongoDAO.findOfficeExpensesByDate(date, pageable);
        serviceUtils.fillInUserNames(officeExpenses.getContent());
        return officeExpenses;
    }

    public List<OfficeExpense> findOfficeExpenses(JSONObject query, Pageable pageable) throws ParseException {
        List<OfficeExpense> expenses = officeExpenseMongoDAO.searchOfficeExpenses(query,pageable);
        serviceUtils.fillInUserNames(expenses);
        return expenses;
    }
}
