package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.impl.BusServiceMongoDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.BusService;
import com.mybus.model.BusServicePublishStatus;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.model.ServiceBoardingPoint;
import com.mybus.model.ServiceDropingPoint;
import com.mybus.model.ServiceFare;
import com.mybus.model.ServiceFrequency;


import java.util.ArrayList;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private RouteDAO routeDAO;
	
	@Autowired
	private CityDAO cityDAO;
	
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
		if(BusServicePublishStatus.PUBLISHED.name().equalsIgnoreCase(busService.getStatus())){
			busService.setId(null);
		}
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
		Preconditions.checkNotNull(routeDAO.findOne(busService.getRouteId()), "Invalid route id");
		Preconditions.checkNotNull(busService.getEffectiveFrom(), "The bus service start date can not be null");
		Preconditions.checkNotNull(busService.getEffectiveTo(), "The bus service end date not be null");
		if(busService.getEffectiveFrom().isAfter(busService.getEffectiveTo())){
			throw new RuntimeException("Invalid service dates. FROM date can not be after TO date");
		}
		Preconditions.checkNotNull(busService.getFrequency(), "The bus service frequency can not be null");
		if(busService.getFrequency().equals(ServiceFrequency.WEEKLY)){
			Preconditions.checkNotNull(busService.getWeeklyDays(), "Weekly days can not be null");
		} else if(busService.getFrequency().equals(ServiceFrequency.SPECIAL)){
			Preconditions.checkNotNull(busService.getSpecialServiceDates(), "Weekly days can not be null");
		}

		//TODO: validate the service fares

		//TODO validate the boarding and dropping points

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

	public BusService publishBusService(String id){
		BusService busService = busServiceDAO.findOne(id);
		Preconditions.checkNotNull(busService, "We don't have this bus service");
		if(BusServicePublishStatus.PUBLISHED.name().equalsIgnoreCase(busService.getStatus())){
			throw new RuntimeException("This bus service already published");
		} else if(BusServicePublishStatus.IN_ACTIVE.name().equalsIgnoreCase(busService.getStatus())){
			throw new RuntimeException("This bus service is in In-Active State,You Can not publish !");
		}
		busService.setStatus(BusServicePublishStatus.PUBLISHED.name());
		return busServiceDAO.save(busService);
	}

	public BusService updateServiceConfiguration(BusService service) {
		
		Route route = routeDAO.findOne(service.getRouteId());
		
		City fromCity = cityDAO.findOne(route.getFromCity());
		Set<ServiceBoardingPoint> boardingPointsSet = new LinkedHashSet<ServiceBoardingPoint>();
		ServiceBoardingPoint sbp = null;
		for(BoardingPoint bp :fromCity.getBoardingPoints()){
			if(bp.isActive()) {
				sbp = new ServiceBoardingPoint();
				sbp.setBoardingPointId(bp.getId());
				boardingPointsSet.add(sbp);
			}
		}
		
		
		City toCity = cityDAO.findOne(route.getToCity());
		Set<ServiceDropingPoint> dropingPointsSet = new LinkedHashSet<ServiceDropingPoint>();
		ServiceDropingPoint sdp = null; 
		for(BoardingPoint dp :toCity.getBoardingPoints()) {
			if(dp.isActive() && dp.isDroppingPoint()) {
				sdp = new ServiceDropingPoint();
				sdp.setDroppingPointId(dp.getId());
				dropingPointsSet.add(sdp);
			}
		}
		
		//Assumed via cities is in source to destination order in List
		City viaCity = null;
		
		ServiceFare sf = null;
		List<ServiceFare> sfList =  new ArrayList<ServiceFare>();
		List<City> preViaCityList = new LinkedList<City>();
		for(String cityID:route.getViaCities()) {
			viaCity = cityDAO.findOne(cityID);
			if(viaCity.isActive()) {
				
				sf = new ServiceFare();
				sf.setSourceCityId(route.getFromCity());
				sf.setDestinationCityId(viaCity.getId());
				sf.setActive(false);
				sfList.add(sf);
				
				sf = new ServiceFare();
				sf.setSourceCityId(viaCity.getId());
				sf.setDestinationCityId(route.getToCity());
				sf.setActive(false);
				sfList.add(sf);
				
				sf = new ServiceFare();
				if(preViaCityList.size()>=0){
					for(City preViaCity:preViaCityList){
						sf.setSourceCityId(preViaCity.getId());
						sf.setDestinationCityId(viaCity.getId());
						sf.setActive(false);
						sfList.add(sf);
					}
				}
				
				for(BoardingPoint bp :viaCity.getBoardingPoints()){
					if(bp.isActive()) {
						sbp = new ServiceBoardingPoint();
						sbp.setBoardingPointId(bp.getId());
						boardingPointsSet.add(sbp);
					}
					if(bp.isActive() && bp.isDroppingPoint()) {
						sdp = new ServiceDropingPoint();
						sdp.setDroppingPointId(bp.getId());
						dropingPointsSet.add(sdp);
					}
				}
				preViaCityList.add(viaCity);
			}
		}
		
		sf = new ServiceFare();
		sf.setSourceCityId(route.getFromCity());
		sf.setDestinationCityId(route.getToCity());
		sf.setActive(true);
		sfList.add(sf);
		
		service.setBoardingPoints(boardingPointsSet);
		service.setDropingPoints(dropingPointsSet);
		service.setServiceFares(sfList);
		
		return service;
	}
}
