package com.mybus.service;
 
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.model.*;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BusServiceManagerTest extends AbstractControllerIntegrationTest {
	
	@Autowired
	private BusServiceManager busServiceManager;

	@Autowired
	private CityManager cityManager;

	@Autowired
	private RouteManager routeManager;

	@Autowired
	private LayoutManager layoutManager;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private CityDAO cityDAO;

	@Autowired
	private LayoutDAO layoutDAO;
	@Autowired
	private BusServiceDAO busServiceDAO;
	
	private void cleanup(){
		cityDAO.deleteAll();
		routeDAO.deleteAll();
		layoutDAO.deleteAll();
		busServiceDAO.deleteAll();
	}

	@Before
	@After
	public void setup(){
		cleanup();
	}

	@Test
	public void testCreateService() {
		BusService service = createBusService();
		service = busServiceManager.saveBusService(service);
		Assert.assertNotNull(service.getId());
	}

	@Test
	public void testUpdateServiceConfiguration() {
		
		BusService service = createBusService();
		service = busServiceManager.updateServiceConfiguration(service);
		Assert.assertNotNull(service);
		Assert.assertNotSame(0, service.getBoardingPoints());
		Assert.assertNotSame(0, service.getDropingPoints());
		
		Assert.assertEquals(3, service.getServiceFares().size());
		Assert.assertEquals(4, service.getBoardingPoints().size());
		Assert.assertEquals(4, service.getDropingPoints().size());
	}


	private BusService createBusService() {
		
		BusService service = new BusService();

		City fromCity = new City("FromCity", "TestState", true, new ArrayList<>());
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp1", "landmark", "123", true,true));
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp2", "landmark", "123", true,true));
		fromCity = cityManager.saveCity(fromCity);

		City toCity = new City("ToCity", "TestState", true, new ArrayList<>());
		toCity.getBoardingPoints().add(new BoardingPoint("tocity-bp1", "landmark", "123", true,true));
		toCity.getBoardingPoints().add(new BoardingPoint("tocity-bp2", "landmark", "123", true,true));
		toCity = cityManager.saveCity(toCity);

		Set<String> viaCitySet = new HashSet<String>();
		City viaCity = new City("ViaCity", "TestState", true, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity-bp1", "landmark", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity-bp2", "landmark", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());

		viaCity = new City("ViaCity2", "TestState2", false, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity2-bp1", "landmark2", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity2-bp2", "landmark2", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());
		
		viaCity = new City("ViaCity3", "TestState3", false, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity3-bp1", "landmark3", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity3-bp2", "landmark3", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());
		
		JSONObject routeJSON = new JSONObject();
		routeJSON.put("name", "To to From");
		routeJSON.put("fromCity", fromCity.getId());
		routeJSON.put("toCity", toCity.getId());
		routeJSON.put("viaCities",viaCitySet);
		Route route = routeManager.saveRoute(new Route(routeJSON));
		service.setRouteId(route.getId());

		Layout layout = new Layout();
		layout.setName("AC Sleeper");
		layout = layoutManager.saveLayout(layout);
		service.setLayoutId(layout.getId());
		service.setCutoffTime(30);
		DateTime fromDate = new DateTime();
		DateTime toDate = fromDate.plusDays(10);
		service.setEffectiveFrom(fromDate);
		service.setEffectiveTo(toDate);
		service.setFrequency(ServiceFrequency.DAILY);
		service.setPhoneEnquiry("1232432");
		service.setServiceName("TestService");
		service.setServiceNumber("1231");
		service.setServiceTax(10.0);
		return service;
	}


}
