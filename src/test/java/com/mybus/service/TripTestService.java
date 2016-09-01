package com.mybus.service;

import com.mybus.model.City;
import com.mybus.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by srinikandula on 8/31/16.
 */
@Service
public class TripTestService {
    @Autowired
    private CityTestService cityTestService;

    @Autowired
    private TripManager tripManager;

    public Trip createTestTrip() {
        City fromCity = cityTestService.createTestCity();
        City toCity = cityTestService.createTestCity();
        Trip trip = new Trip();
        trip.setFromCityId(fromCity.getId());
        trip.setToCityId(toCity.getId());
        return tripManager.saveTrip(trip);
    }
}
