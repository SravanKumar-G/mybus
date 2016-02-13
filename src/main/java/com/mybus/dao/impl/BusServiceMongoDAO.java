package com.mybus.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mybus.dao.BusServiceDAO;
import com.mybus.model.BusService;
import com.mybus.service.SessionManager;

/**
 * Created by schanda on 02/02/16.
 */
@Repository
public class BusServiceMongoDAO {

	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private SessionManager sessionManager;

	public BusService save(BusService busService) {
		return busServiceDAO.save(busService);
	}

	public BusService update(BusService busService) throws Exception {
		BusService dbCopy = busServiceDAO.findOne(busService.getId());
		dbCopy.merge(busService);
		return busServiceDAO.save(dbCopy);
	}

}
