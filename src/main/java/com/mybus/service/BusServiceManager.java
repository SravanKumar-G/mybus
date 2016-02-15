package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.impl.BusServiceMongoDAO;
import com.mybus.model.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by schanda on 02/02/16.
 */
@Service
public class BusServiceManager {

	private static final Logger logger = LoggerFactory.getLogger(BusServiceManager.class);

	@Autowired
	private BusServiceMongoDAO busServiceMongoDAO;

	@Autowired
	private BusServiceDAO busServiceDAO;

	public boolean deleteService(String id) {
		Preconditions.checkNotNull(id, "The Service id can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Service :[{}]" + id);
		}
		if (busServiceDAO.findOne(id) != null) {
			busServiceDAO.delete(id);
		} else {
			throw new RuntimeException("Unknown service id");
		}
		return true;
	}

	public BusService updateBusService(BusService busService) {
		Preconditions.checkNotNull(busService, "The bus service can not be null");
		Preconditions.checkNotNull(busService.getServiceName(), "The bus service name can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Saving bus service :[{}]" + busService);
		}
		BusService busServiceUpdated = null;
		try {
			busServiceUpdated = busServiceMongoDAO.update(busService);
		} catch (Exception e) {
			throw new RuntimeException("error updating bus service ", e);
		}
		return busServiceUpdated;
	}

	public BusService saveBusService(BusService busService) {
		Preconditions.checkNotNull(busService, "The bus service can not be null");
		Preconditions.checkNotNull(busService.getServiceName(), "The bus service name can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Saving bus service :[{}]" + busService);
		}
		return busServiceDAO.save(busService);
	}

}
