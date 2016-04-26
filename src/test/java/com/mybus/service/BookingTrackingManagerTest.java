package com.mybus.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BookingTrakingDAO;
import com.mybus.model.BookingTracking;

public class BookingTrackingManagerTest extends AbstractControllerIntegrationTest {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(BookingTrackingManagerTest.class);
	
	@Autowired
	private BookingTrakingDAO bookingTrakingDAO;
	
	@Test
	public void addBookingTraking(){
		BookingTracking bk = new BookingTracking();
		bk.setMerchantRefNo("asfasdf");
		bk.setBookingId("dsgdf");
		BookingTracking bk1 = bookingTrakingDAO.save(bk);
		LOGGER.info("this is test"+bk1.getMerchantRefNo());
	}
}
