package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mybus.model.Trip;

public interface TripDAO extends PagingAndSortingRepository<Trip, String> {
	
}
