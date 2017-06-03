package com.mybus.service;

import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.impl.OfficeExpenseMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OfficeExpense;
import com.mybus.model.Payment;
import com.mybus.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/12/16.
 */

@Service
public class OfficeExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(OfficeExpenseManager.class);

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OfficeExpenseMongoDAO officeExpenseMongoDAO;

    @Autowired
    private UserManager userManager;

    public OfficeExpense save(OfficeExpense officeExpense) {
        return officeExpenseMongoDAO.save(officeExpense);
    }

    public OfficeExpense updateOfficeExpense(OfficeExpense officeExpense) {
        logger.debug("updating balance for office:" + officeExpense.getBranchOfficeId());
        if(officeExpense.getStatus() != null && (officeExpense.getStatus().equals(Payment.STATUS_APPROVED))){
            User thatUser = userManager.findOne(officeExpense.getCreatedBy());
            thatUser.setAmountToBePaid(thatUser.getAmountToBePaid()-officeExpense.getAmount());
            userManager.saveUser(thatUser);
        }
        return officeExpenseDAO.save(officeExpense);
    }


    public void delete(String id) {
        OfficeExpense officeExpense = officeExpenseDAO.findOne(id);
        if(officeExpense != null && officeExpense.getStatus() != null) {
            throw new BadRequestException("officeExpense can not be deleted");
        }
        officeExpenseDAO.delete(officeExpense);
    }


    private void loadUserNames(List<OfficeExpense> officeExpenses) {
        Map<String, String> userNames = userManager.getUserNames(true);
        officeExpenses.forEach(officeExpense -> {
            loadUserNames(userNames, officeExpense);
        });
    }

    private void loadUserNames(Map<String, String> userNames, OfficeExpense officeExpense) {
        officeExpense.getAttributes().put("createdBy", userNames.get(officeExpense.getCreatedBy()));
        officeExpense.getAttributes().put("updatedBy", userNames.get(officeExpense.getUpdatedBy()));
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
        loadUserNames(userManager.getUserNames(true), payment);
        return payment;
    }

    public Page<OfficeExpense> findOfficeExpenseByDate(String date, Pageable pageable) {
        Page<OfficeExpense> officeExpenses = officeExpenseMongoDAO.findOfficeExpensesByDate(date, pageable);
        Map<String,String> userNames = userManager.getUserNames(true);
        for(OfficeExpense officeExpense:officeExpenses.getContent()) {
            officeExpense.getAttributes().put("createdBy", userNames.get(officeExpense.getCreatedBy()));
        }
        return officeExpenses;
    }
}
