package com.mybus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mybus.dao.BookingTrakingDAO;
import com.mybus.model.BookingTracking;

@Service
public class BookingTrackingManager {

	@Autowired
	private BookingTrakingDAO bookingTrakingDAO;
	
	public void savePayment(BookingTracking bookingTracking){
		bookingTrakingDAO.save(bookingTracking);
	}
}
