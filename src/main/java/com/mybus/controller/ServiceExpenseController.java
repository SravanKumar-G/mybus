package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.model.ServiceExpense;
import com.mybus.service.ServiceExpenseManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/serviceExpense")
public class ServiceExpenseController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseController.class);

	@Autowired
	private ServiceExpenseManager serviceExpenseManager;

	@Autowired
	private ServiceExpenseDAO serviceExpenseDAO;

	@RequestMapping(value = "byDate", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public List<ServiceExpense> getAll(HttpServletRequest request,
									   @ApiParam(value = "Date of travel") @RequestParam final String travelDate) throws ParseException {
		return serviceExpenseManager.getServiceExpenses(travelDate);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public ServiceExpense get(HttpServletRequest request,
									   @ApiParam(value = "Date of travel") @PathVariable final String id) throws ParseException {
		return serviceExpenseManager.getServiceExpense(id);
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public ServiceExpense getAll(HttpServletRequest request,
								 @RequestBody final ServiceExpense serviceExpense) throws ParseException {
		return serviceExpenseManager.save(serviceExpense);
	}

}
