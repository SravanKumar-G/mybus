package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.BusServiceDAO;
import com.mybus.model.BusService;
import com.mybus.service.BusServiceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
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
@Api(value = "BusServiceController", description = "Management of the Bus Services ")
public class BusServiceController extends MyBusBaseController{

	private static final Logger logger = LoggerFactory.getLogger(BusServiceController.class);

	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private BusServiceManager busServiceManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "services", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get all the bus services available", response = BusService.class, responseContainer = "List")
	public Iterable<BusService> getServices(HttpServletRequest request) {
		return busServiceDAO.findAll();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Create a bus service", response = BusService.class)
	public BusService createService(HttpServletRequest request,
			@ApiParam(value = "JSON for BusService to be created") @RequestBody final BusService busService) {
		logger.debug("post bus service called");
		return busServiceManager.saveBusService(busServiceManager.convertStringToDatesInBusService(busService));
	}

	@RequestMapping(value = "service/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get the BusService JSON", response = BusService.class)
	public BusService getService(HttpServletRequest request,
			@ApiParam(value = "Id of the BusService to be found") @PathVariable final String id) {
		logger.debug("get bus service called");
		return busServiceDAO.findOne(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a BusService")
	public JSONObject deleteService(HttpServletRequest request,
			@ApiParam(value = "Id of the bus service to be deleted") @PathVariable final String id) {
		logger.debug("get bus service called");
		JSONObject response = new JSONObject();
		response.put("deleted", busServiceManager.deleteService(id));
		return response;
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Update a BusService", response = BusService.class)
	public BusService updateService(HttpServletRequest request,
			@ApiParam(value = "JSON for BusService") @RequestBody final BusService service) {
		logger.debug("update BusService called");
		return busServiceManager.updateBusService(service);
	}

}
