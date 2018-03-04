package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.AmenityDAO;
import com.mybus.dao.FillingStationDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Amenity;
import com.mybus.model.FillingStation;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FillingStationManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FillingStationManager.class);
	
	@Autowired
	private FillingStationDAO fillingStationDAO;

	@Autowired
	private SessionManager sessionManager;

	public Iterable<FillingStation> findAll(){
		List<FillingStation> fillingStations;
		if(sessionManager.getOperatorId() == null){
			throw new BadRequestException("No operator found in session");
		}
		return fillingStationDAO.findByOperatorId(sessionManager.getOperatorId());

	}
	
	public FillingStation save(FillingStation fillingStation){
		fillingStation.validate();
		fillingStation.setOperatorId(sessionManager.getOperatorId());
		return fillingStationDAO.save(fillingStation);
	}
	
	public FillingStation upate(FillingStation fillingStation){
		Preconditions.checkNotNull(fillingStation, "The FillingStation can not be null");
		Preconditions.checkNotNull(fillingStation.getId(), "The FillingStation id can not be null");
		FillingStation a = fillingStationDAO.findOne(fillingStation.getId());
		try {
			a.merge(fillingStation);
			fillingStationDAO.save(a);
		} catch (Exception e) {
			LOGGER.error("Error updating the Route ", e);
	        throw new RuntimeException(e);
		}
		return a;
	}

	public boolean delete(String id){
		Preconditions.checkNotNull(id, "The FillingStation id can not be null");
		fillingStationDAO.delete(id);
		return true;
	}
	public void deleteAll() {
		fillingStationDAO.deleteAll();
	}

	public long count() {
		return fillingStationDAO.count();
	}

	public FillingStation findOne(String id) {
		return fillingStationDAO.findOne(id);
	}
}
