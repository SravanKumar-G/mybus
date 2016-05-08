package com.mybus.service;

import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.TripDAO;
import com.mybus.model.BusService;
import com.mybus.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yks-Srinivas
 *
 */
@Service
public class TripManager {
	
	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private BusServiceDAO busServiceDAO;
	
	public Iterable<Trip> getAllTrips(){
		return tripDAO.findAll();	
	}
	
	public Trip createTrip(Trip trip){
		return tripDAO.save(trip);	
	}
	
	public Trip getTripByID(String tripID){
		return tripDAO.findOne(tripID);	
	}
	/*
		The trips should be created for the service is being published.
		We may have to make this asynchronus call
	 */
	public void publishService(String serviceId){
		BusService service = busServiceDAO.findOne(serviceId);

	}
}
