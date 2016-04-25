package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mybus.model.BookingTracking;

@Repository
public interface BookingTrakingDAO extends  PagingAndSortingRepository<BookingTracking, String> {
	
}
