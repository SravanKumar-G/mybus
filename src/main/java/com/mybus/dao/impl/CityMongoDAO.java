package com.mybus.dao.impl;

import com.mybus.dao.CityDAO;
import com.mybus.model.City;
import com.mybus.service.SessionManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class CityMongoDAO {
    
    @Autowired
    private CityDAO cityDAO;
    
    @Autowired
    private SessionManager sessionManager;

    public City save(City city){
        city.setCreatedAt(new DateTime());
        city.setCreatedBy(sessionManager.getCurrentUser().getFirstName());
        return cityDAO.save(city);
    }
    public City update(City city){
        city.setUpdatedAt(new DateTime());
        city.setUpdatedBy(sessionManager.getCurrentUser().getFirstName());
        return cityDAO.save(city);
    }

}
