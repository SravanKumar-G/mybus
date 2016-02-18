package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.UserDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.User;
import com.mybus.model.Vehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by schanda on 1/17/16.
 */

public class vehicleControllerTest extends AbstractControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private VehicleDAO vehicleDAO;

	@Autowired
	private UserDAO userDAO;

	private User currentUser;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
		currentUser = new User("test", "test", "test", "test", true, true);
		cleanup();
		currentUser = userDAO.save(currentUser);
	}

	private void cleanup() {
		vehicleDAO.deleteAll();
		userDAO.deleteAll();
	}

	@After
	public void teardown() {
//		cleanup();
	}

	@Test
	public void testGetAllVehiclesSuccess() throws Exception {
		Vehicle vehicle1 = new Vehicle("Bus", "Volvo", "Ap 09 CF 1817", true);
		mockMvc.perform(asUser(
				post("/api/v1/vehicle").content(objectMapper.writeValueAsBytes(vehicle1)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		Vehicle vehicle2 = new Vehicle("Bus", "Benz", "Ap 28 AP 0864", true);
		mockMvc.perform(asUser(
				post("/api/v1/vehicle").content(objectMapper.writeValueAsBytes(vehicle2)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		ResultActions actions = mockMvc.perform(asUser(get("/api/v1/vehicles").contentType(MediaType.APPLICATION_JSON),
				currentUser));
		actions.andExpect(jsonPath("$[0].active").value(true));
		actions.andExpect(jsonPath("$[0].type").value("Bus"));
		actions.andExpect(jsonPath("$[0].description").value("Volvo"));
		actions.andExpect(jsonPath("$[0].regNo").value("Ap 09 CF 1817"));

		actions.andExpect(jsonPath("$[1].active").value(true));
		actions.andExpect(jsonPath("$[1].type").value("Bus"));
		actions.andExpect(jsonPath("$[1].description").value("Benz"));
		actions.andExpect(jsonPath("$[1].regNo").value("Ap 28 AP 0864"));
	}

}