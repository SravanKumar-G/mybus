package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.ExpenseDAO;
import com.mybus.dao.impl.ExpenseDAOImpl;
import com.mybus.model.Expense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/api/v1/")
public class ExpensesController {
    private static final Logger logger = LoggerFactory.getLogger(ExpensesController.class);

    @Autowired
    private ExpenseDAOImpl expenseDAOImpl;

    @Autowired
    private ExpenseDAO expenseDAO;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "expenses", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public Iterable<Expense> getUserInfo(HttpServletRequest request) {
        return expenseDAO.findAll();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "expense", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Expense createCity(HttpServletRequest request, @RequestBody final Expense expense) {
        logger.debug("post expense called");
        return expenseDAOImpl.save(expense);
    }

}
