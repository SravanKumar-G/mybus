package com.mybus.dao.impl;

import com.mybus.dao.ExpenseDAO;
import com.mybus.model.Expense;
import com.mybus.service.SessionManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class ExpenseDAOImpl {
    @Autowired
    private ExpenseDAO expenseDAO;
    @Autowired
    private SessionManager sessionManager;

    public Expense save(Expense expense){
        expense.setCreatedAt(new DateTime());
        expense.setCreatedBy(sessionManager.getCurrentUser().getFirstName());
        return expenseDAO.save(expense);
    }
    public Expense update(Expense expense){
        expense.setUpdatedAt(new DateTime());
        expense.setUpdatedBy(sessionManager.getCurrentUser().getFirstName());
        return expenseDAO.save(expense);
    }

}
