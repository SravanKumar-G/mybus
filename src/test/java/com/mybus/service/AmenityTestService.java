package com.mybus.service;

import com.mybus.model.Amenity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by srinikandula on 9/25/16.
 */
@Service
public class AmenityTestService {
    @Autowired
    private AmenitiesManager amenitiesManager;

    public Amenity createTestAmenity() {
        Amenity amenity = new Amenity();
        amenity.setName("bottle");
        amenity.setActive(true);
        return amenitiesManager.save(amenity);
    }
}
