package com.mybus.service;

import com.mybus.test.util.AmenityTestService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.AmenityDAO;
import com.mybus.model.Amenity;


/**
 * 
 * @author yks-Srinivas
 *
 */

public class AmenitiesManagerTest extends AbstractControllerIntegrationTest {

	@Autowired
	public AmenityDAO amenityDAO;

	@Autowired
	private AmenityTestService amenityTestService;

	@Autowired
	public AmenitiesManager amenitiesManager;

	private void cleanup() {
		amenityDAO.deleteAll();
	}

	@After
	@Before
	public void teardown() {
		cleanup();
	}

	@Test
	public void amenitTest(){
		Amenity a = amenityTestService.createTestAmenity();
		Assert.assertNotNull(a);
		Assert.assertNotNull(a.getId());
		
		Assert.assertEquals(true, a.isActive());
		Assert.assertEquals("bottle", a.getName());
		
		a.setActive(false);
		Amenity a1 = amenitiesManager.upateAmenity(a);
		
		Assert.assertEquals(a1.getId(), a.getId());
		Assert.assertEquals(false, a.isActive());
		
		Assert.assertEquals(a1, amenitiesManager.getAmenityById(a1.getId()));
	}
}
