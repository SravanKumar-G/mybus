package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.FillingStation;
import com.mybus.service.FillingStationManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/fillingStations")
@Api(value="FillingStationController")
public class FillingStationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FillingStationController.class);

	@Autowired
	private FillingStationManager fillingStationManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the fillingStations available", response = Page.class, responseContainer = "List")
	public Iterable<FillingStation> getAll() {
		LOGGER.debug("Get all the fillingStations available");
		return fillingStationManager.findAll();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@ApiOperation(value = "Get fillingStations count")
	public long count() {
		return fillingStationManager.count();
	}


	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add fillingStation")
	public FillingStation addFillingStation(HttpServletRequest request,@RequestBody FillingStation fillingStation) {
		LOGGER.debug("add fillingStation");
		return fillingStationManager.save(fillingStation);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Update fillingStation")
	public FillingStation updateFillingStation(HttpServletRequest request,@RequestBody FillingStation fillingStation) {
		LOGGER.debug("update fillingStation");
		return fillingStationManager.upate(fillingStation);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get fillingStations by id")
	public FillingStation getById(HttpServletRequest request,
			@ApiParam(value = "Id of the amenity to be deleted") @PathVariable final String id) {
		LOGGER.debug("get amenity by id");
		return fillingStationManager.findOne(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete a fillingStation")
	public JSONObject deleteFillingStation(HttpServletRequest request,
			@ApiParam(value = "Id of the fillingStation to be deleted") @PathVariable final String id) {
		LOGGER.debug("delete fillingStation called");
		JSONObject response = new JSONObject();
		response.put("deleted", fillingStationManager.delete(id));
		return response;
	}
}