package com.mybus.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.model.BusJourney;
import com.mybus.model.BusService;
import com.mybus.model.City;
import com.mybus.model.JourneyType;
import com.mybus.model.Layout;
import com.mybus.model.Trip;
import com.mybus.service.BookingSessionInfo;
import com.mybus.service.BookingSessionManager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
/**
 * Ticket booking flow controller Using like 
 * get stations, get toStations, get available buses, get buslayout, block ticket,
 * and book ticket 
 */

@RestController
@RequestMapping(value = "/api/v1/")
public class BusTicketBookingController {
	
	@Autowired
	private CityDAO cityDAO;
	
	@Autowired
	private BusServiceDAO busServiceDAO;
	
	@Autowired
	private LayoutDAO layoutDAO;
	
	@Autowired
	private BookingSessionManager bookingSessionManager; 
	
	@RequiresAuthorizedUser(value=false)
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "stations", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the cities available", response = City.class, responseContainer = "List")
	public Iterable<City>  getStations(){
		return cityDAO.findAll();
	}
	
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "searchForBus", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="creating booking sesssion info")
	public BookingSessionInfo searchForBus(
			@RequestParam("fromCity") String fromCity,
			@RequestParam("toCity") String ToCity,
			@RequestParam("dateOfJourney") String dateOfJourney,
			@RequestParam("returnJourney") String returnJourney,
			@RequestParam("journeyType") JourneyType journeyType) {
		
		BookingSessionInfo bookingSessionInfo = new BookingSessionInfo();
		bookingSessionInfo.setBusJourney(fromCity,ToCity,dateOfJourney,returnJourney,journeyType);
		bookingSessionManager.setBookingSessionInfo(bookingSessionInfo);
		return bookingSessionInfo;
	}
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "getsearchForBus", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the BookingSessionInfo JSON")
	public BookingSessionInfo getSearchForBus() {
		return bookingSessionManager.getBookingSessionInfo();
	}
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "availabletrip", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class, responseContainer = "List")
	public List<Trip> getAvailableTrip(@RequestParam("journeyType") JourneyType journeyType) {
		List<Trip> availableTrips = null;
		BookingSessionInfo BookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		if(JourneyType.ONE_WAY.equals(journeyType)){
			BusJourney busJourney= BookingSessionInfo.getBusJournies().get(0);
			availableTrips = availableTrips(busJourney.getFromCity(),busJourney.getToCity(),busJourney.getDateOfJourney());
		}else{
			BusJourney busJourney= BookingSessionInfo.getBusJournies().get(1);
			availableTrips = availableTrips(busJourney.getFromCity(),busJourney.getToCity(),busJourney.getDateOfJourney());
		}
		return availableTrips;
	}
	public List<Trip> availableTrips(String fromCity,String ToCity, String dateOfJourney){
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
	@ApiOperation(value ="Get the bus Layout JSON", response = Trip.class)
	public Trip getTripLayout(HttpServletRequest request,@ApiParam(value = "Id of the layout to be found") @PathVariable final String layoutId) {
		return tripLayout(layoutId);
	}
	
	public Trip tripLayout(String layoutId){
		Trip t = new Trip();
		Layout layout = layoutDAO.findOne(layoutId);
		t.setRows(layout.getRows());
		return t; 
	}
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "blockSeat", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	public BusJourney blockSeat(HttpServletRequest request,@RequestBody BusJourney busJourney) {
		BookingSessionInfo BookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		List<BusJourney> busJourneyList = BookingSessionInfo.getBusJournies();
		if(JourneyType.ONE_WAY.equals(busJourney.getJourneyType())){
			 BusJourney busJourneyFromSession = busJourneyList.get(0);
			 busJourneyFromSession= busJourney;
		}
		return busJourney;
	}
	
}
