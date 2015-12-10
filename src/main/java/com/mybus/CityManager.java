package com.mybus;

import com.mybus.dao.impl.CityMongoDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 12/9/15.
 */
@Service
public class CityManager {
    @Autowired
    private CityMongoDAO cityMongoDAO;

    public City addBoardingPointToCity(String cityId, BoardingPoint bp) {
        return cityMongoDAO.addBoardingPoint(cityId, bp);
    }

}
