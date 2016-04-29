package com.mybus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mybus.dao.TripDAO;
import com.mybus.model.Trip;

/**
 * 
 * @author yks-Srinivas
 *
 */
@Service
public class TripManager {
	
	@Autowired
	private TripDAO tripDAO;
	
	public Iterable<Trip> getAllTrips(){
		return tripDAO.findAll();	
	}
	
	public Trip createTrip(Trip trip){
		return tripDAO.save(trip);	
	}
	
	public Trip getTripByID(String tripID){
		return tripDAO.findOne(tripID);	
	}
}
