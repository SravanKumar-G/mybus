package com.mybus.dao;

import com.mybus.model.Trip;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TripDAO extends PagingAndSortingRepository<Trip, String> {
	
}
