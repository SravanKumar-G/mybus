package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.model.BusService;
import com.mybus.model.Layout;
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

import java.util.ArrayList;
import java.util.List;

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
	
	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private LayoutDAO layoutDAO;
	
	
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
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "availabletrip", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class, responseContainer = "List")
	public List<Trip> getVailableTrip(HttpServletRequest request) {
		return availableTrips(null,null);
	}
	public List<Trip> availableTrips(String fromCityId,String ToCity){
		List<Trip> trips = new ArrayList<Trip>();
		Iterable<BusService> busAllServie =busServiceDAO.findAll();
		busAllServie.forEach(bs->{
			Trip t = new Trip();
			t.setActive(true);
			t.setServiceName(bs.getServiceName());
			t.setServiceNumber(bs.getServiceNumber());
			t.setAmenities(bs.getAmenities());
			t.setServiceFares(bs.getServiceFares());
			t.setServiceId(bs.getServiceNumber());
			t.setRouteId(bs.getRouteId());
			t.setLayoutId(bs.getLayoutId());
			t.setBoardingPoints(bs.getBoardingPoints());
			t.setDropingPoints(bs.getDropingPoints());
			
			trips.add(t);
		});
		return trips;
		
	}
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "busLayout/{layoutId}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class)
	public Trip getTripLayout(HttpServletRequest request,@ApiParam(value = "Id of the trip to be found") @PathVariable final String layoutId) {
		return tripLayout(layoutId);
	}
	
	public Trip tripLayout(String layoutId){
		Trip t = new Trip();
		Layout layout = layoutDAO.findOne(layoutId);
		t.setRows(layout.getRows());
		return t; 
	}

}
