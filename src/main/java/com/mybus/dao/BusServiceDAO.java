package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mybus.model.BusService;

/**
 * Created by schanda on 02/02/16.
 */
@Repository
public interface BusServiceDAO extends PagingAndSortingRepository<BusService, String> {
	
	BusService findOneByName(String name);

	void delete(String s);
}
