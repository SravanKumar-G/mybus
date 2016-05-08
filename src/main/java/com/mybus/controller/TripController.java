package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Trip;
import com.mybus.service.TripManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
/**
 * 
 * @author yks-Srinivas
 *
 */
@Controller
@RequestMapping(value = "/api/v1/")
public class TripController {

	@Autowired
	private TripManager tripManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "trips", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get all the Trips available", response = Trip.class, responseContainer = "List")
	public Iterable<Trip> getTrips(HttpServletRequest request) {
		return tripManager.getAllTrips();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "trip", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
	consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Create a Trip")
	public ResponseEntity createTrip(HttpServletRequest request,
			@ApiParam(value = "JSON for Trip to be created") @RequestBody final Trip trip){
		return new ResponseEntity<>(tripManager.createTrip(trip), HttpStatus.OK);
	}

	@RequestMapping(value = "trip/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class)
	public Trip getTripByID(HttpServletRequest request,
			@ApiParam(value = "Id of the trip to be found") @PathVariable final String id) {
		return tripManager.getTripByID(id);
	}

}
