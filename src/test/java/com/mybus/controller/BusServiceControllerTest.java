package com.mybus.controller;

import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BusService;
import com.mybus.model.City;
import com.mybus.model.Layout;
import com.mybus.model.Route;
import com.mybus.model.User;
import com.mybus.service.BusServiceManager;
import com.mybus.service.CityManager;
import com.mybus.service.RouteManager;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;

public class BusServiceControllerTest extends AbstractControllerIntegrationTest{

    @Autowired
    private BusServiceManager busServiceManager;

    @Autowired
    private BusServiceDAO busServiceDAO;
    
    @Autowired
    private LayoutDAO layoutDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    public CityManager cityManager;
    
    @Autowired
    public RouteManager routeManager;
    
    private MockMvc mockMvc;
    private User currentUser;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    @After
    public void teardown(){
        cleanup();
    }
    private void cleanup() {
        userDAO.deleteAll();
        busServiceDAO.deleteAll();
    }
    
    public Layout saveLayout(){
    	Layout layout = new Layout();
    	layout.setActive(true);
    	layout.setMiddleRowLastSeat(true);
    	layout.setMiddleRowPosition(1);
    	layout.setName("sleeper");
    	layout.setSeatsPerRow(11);
    	layout = layoutDAO.save(layout);
    	return layout;
    }
    private Route createTestRoute() {
        City fromCity = new City("FromCity"+new ObjectId().toString(), "state", true, new ArrayList<>());
        City toCity = new City("ToCity"+new ObjectId().toString(), "state", true, new ArrayList<>());

        cityManager.saveCity(fromCity);
        cityManager.saveCity(toCity);
        Route route = new Route("TestRoute"+new ObjectId().toString(), fromCity.getId(),
                toCity.getId(), new LinkedHashSet<>(), true);
        return routeManager.saveRoute(route);
    }
    @Test
    public void testCreateBusService() throws Exception {


        JSONObject service = new JSONObject();
        
        service.put("active", true);
        service.put("serviceName", "TestName");
        service.put("serviceNumber", "1234");
        service.put("phoneEnquiry", "1234");
        service.put("cutoffTime", "3");
        service.put("serviceTaxType", "PERCENTAGE");
        
        JSONObject schedule = new JSONObject();
        schedule.put("startDate", "2016-01-02");
        schedule.put("endDate", "2017-01-03");
        schedule.put("frequency", "DAILY");
        
        service.put("schedule", schedule);
        service.put("layoutId", saveLayout().getId());
        service.put("routeId", createTestRoute().getId());
        
        
        
        
        /*
        Preconditions.checkNotNull(busService, "The bus service can not be null");
		Preconditions.checkNotNull(busService.getServiceName(), "The bus service name can not be null");
		Preconditions.checkNotNull(busService.getServiceNumber(), "The bus service number can not be null");
		Preconditions.checkNotNull(busService.getPhoneEnquiry(), "The bus service enquiry phone can not be null");
		Preconditions.checkNotNull(busService.getLayoutId(), "The bus service layout can not be null");
		Preconditions.checkNotNull(busService.getEffectiveFrom(), "The bus service start date can not be null");
		Preconditions.checkNotNull(busService.getEffectiveTo(), "The bus service end date not be null");
		Preconditions.checkNotNull(busService.getFrequency(), "The bus service frequency can not be null");
		if(busService.getFrequency().equals(ServiceFrequency.WEEKLY)){
			Preconditions.checkNotNull(busService.getFrequency().getWeeklyDays(), "Weekly days can not be null");
		} else if(busService.getFrequency().equals(ServiceFrequency.SPECIAL)){
			Preconditions.checkNotNull(busService.getFrequency().getSpecialServiceDatesInDate(), "Weekly days can not be null");
		}
         */
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/service")
                .content(service.toJSONString()).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        List<BusService> busServiceList = IteratorUtils.toList(busServiceDAO.findAll().iterator());
        Assert.assertEquals(1, busServiceList.size());
        //actions.andExpect(jsonPath("$.name").value(busServiceList.get));
    }
}
