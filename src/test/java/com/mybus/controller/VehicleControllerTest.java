package com.mybus.controller;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.UserDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.User;
import com.mybus.model.Vehicle;

/**
 * Created by schanda on 1/17/16.
 */

public class VehicleControllerTest extends AbstractControllerIntegrationTest {

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
		cleanup();
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

	@Test
	public void testCreateVehicleSuccess() throws Exception {
		Vehicle vehicle1 = new Vehicle("Bus", "Volvo", "Ap 09 CF 1817", true);
		ResultActions actions = mockMvc.perform(asUser(
				post("/api/v1/vehicle").content(objectMapper.writeValueAsBytes(vehicle1)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.id").exists());
		actions.andExpect(jsonPath("$.createdAt").exists());
		actions.andExpect(jsonPath("$.updatedAt").exists());
		actions.andExpect(jsonPath("$.active").value(true));
		actions.andExpect(jsonPath("$.type").value("Bus"));
		actions.andExpect(jsonPath("$.description").value("Volvo"));
		actions.andExpect(jsonPath("$.regNo").value("Ap 09 CF 1817"));
	}

	@Test
	public void testGetVehicleSuccess() throws Exception {
		Vehicle vehicle1 = new Vehicle("Bus", "Volvo", "Ap 09 CF 1817", true);
		vehicle1.setId("vehicle001");
		mockMvc.perform(asUser(
				post("/api/v1/vehicle").content(objectMapper.writeValueAsBytes(vehicle1)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		ResultActions actions = mockMvc.perform(asUser(get(String.format("/api/v1/vehicle/%s", "vehicle001"))
				.contentType(MediaType.APPLICATION_JSON), currentUser));
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.id").value("vehicle001"));
		actions.andExpect(jsonPath("$.active").value(true));
		actions.andExpect(jsonPath("$.type").value("Bus"));
		actions.andExpect(jsonPath("$.description").value("Volvo"));
		actions.andExpect(jsonPath("$.regNo").value("Ap 09 CF 1817"));
	}

	@Test
	public void testDeleteVehicle() throws Exception {
		Vehicle vehicle1 = new Vehicle("Bus", "Volvo", "Ap 09 CF 1817", true);
		vehicle1.setId("vehicle001");

		Vehicle vehicleSaved = vehicleDAO.save(vehicle1);

		ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/vehicle/%s", vehicleSaved.getId())),
				currentUser));
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.deleted").value(true));
		Assert.assertNull(vehicleDAO.findOne(vehicleSaved.getId()));
	}

	@Test
	public void testUpdateVehicle() throws Exception {
		Vehicle vehicle1 = new Vehicle("Bus", "Volvo", "Ap 09 CF 1817", true);
		vehicle1.setId("vehicle001");

		Vehicle vehicle4Update = vehicleDAO.save(vehicle1);

		vehicle4Update.setDescription("Pulsar");
		vehicle4Update.setRegNo("AP 28 AP 0864");

		ResultActions actions = mockMvc.perform(asUser(
				put("/api/v1/vehicle").content(objectMapper.writeValueAsBytes(vehicle4Update)).contentType(
						MediaType.APPLICATION_JSON), currentUser));
		actions.andExpect(status().isOk());

		actions.andExpect(jsonPath("$.description").value("Pulsar"));
		actions.andExpect(jsonPath("$.regNo").value("AP 28 AP 0864"));
	}

}