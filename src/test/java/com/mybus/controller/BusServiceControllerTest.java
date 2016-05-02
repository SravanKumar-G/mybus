package com.mybus.controller;

import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BusService;
import com.mybus.model.User;
import com.mybus.service.BusServiceManager;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusServiceControllerTest extends AbstractControllerIntegrationTest{

    @Autowired
    private BusServiceManager busServiceManager;

    @Autowired
    private BusServiceDAO busServiceDAO;

    @Autowired
    private UserDAO userDAO;

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

    @Test
    public void testCreateBusService() throws Exception {


        JSONObject service = new JSONObject();
        service.put("serviceName", "TestName");
        service.put("serviceNumber", "1234");
        service.put("phoneEnquiry", "1234");
        service.put("effectiveFrom", "2016-01-02");
        service.put("effectiveTo", "2017-01-03");


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
    }
}
