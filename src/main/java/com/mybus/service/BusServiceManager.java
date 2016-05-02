package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.impl.BusServiceMongoDAO;
import com.mybus.model.BusService;

import com.mybus.model.ServiceFrequency;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

	@Autowired
	private LayoutDAO layoutDAO;
	//TODO: When a service is deleted the respective trips need to be deleted and so does any reservations tied to
	//those trips
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
		validateBusService(busService);
		if (logger.isDebugEnabled()) {
			logger.debug("Updating bus service :[{}]" + busService);
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
		validateBusService(busService);
		if (logger.isDebugEnabled()) {
			logger.debug("Saving bus service :[{}]" + busService);
		}
		return busServiceDAO.save(busService);
	}

	private void validateBusService(BusService busService) {
		Preconditions.checkNotNull(busService, "The bus service can not be null");
		Preconditions.checkNotNull(busService.getServiceName(), "The bus service name can not be null");
		Preconditions.checkNotNull(busService.getServiceNumber(), "The bus service number can not be null");
		Preconditions.checkNotNull(busService.getPhoneEnquiry(), "The bus service enquiry phone can not be null");
		Preconditions.checkNotNull(busService.getLayoutId(), "The bus service layout can not be null");
		Preconditions.checkNotNull(layoutDAO.findOne(busService.getLayoutId()), "Invalid layout id");
		Preconditions.checkNotNull(busService.getEffectiveFrom(), "The bus service start date can not be null");
		Preconditions.checkNotNull(busService.getEffectiveTo(), "The bus service end date not be null");
		Preconditions.checkNotNull(busService.getFrequency(), "The bus service frequency can not be null");
		if(busService.getFrequency().equals(ServiceFrequency.WEEKLY)){
			Preconditions.checkNotNull(busService.getFrequency().getWeeklyDays(), "Weekly days can not be null");
		} else if(busService.getFrequency().equals(ServiceFrequency.SPECIAL)){
			Preconditions.checkNotNull(busService.getFrequency().getSpecialServiceDatesInDate(), "Weekly days can not be null");
		}

		//update
		if (busService.getId() != null ){
			BusService service = busServiceDAO.findOne(busService.getId());
			Preconditions.checkNotNull(service, "Service not found for update");
			BusService servicebyName = busServiceDAO.findOneByServiceName(service.getServiceName());
			if(!service.getId().equals(servicebyName.getId())){
				throw new RuntimeException("A service already exists with the same name");
			}
		} else { //save
			BusService service = busServiceDAO.findOneByServiceName(busService.getServiceName());
			if(service != null) {
				throw new RuntimeException("A service already exists with the same name");
			}
		}
	}
	public BusService convertStringToDatesInBusService(BusService busService){
		Preconditions.checkNotNull(busService.getEffectiveFrom(), "The bus service Effective From can not be null");
		//busService.setEffectiveFromInDate(convertStringToDate(busService.getEffectiveFrom()));
		Preconditions.checkNotNull(busService.getEffectiveTo(), "The bus service Effective To can not be null");
		//busService.setEffectiveToInDate(convertStringToDate(busService.getEffectiveTo()));
		return busService;
	}
	private DateTime convertStringToDate(String stringDate){
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			DateTime dt = formatter.parseDateTime(stringDate);
			return dt;
	}

}
