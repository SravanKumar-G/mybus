package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.User;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 12/9/15.
 */
public class CityControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testCreateCitySuccess() throws Exception {
        JSONObject city = new JSONObject();
        city.put("name", "city");
        city.put("state", "CA");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value("city"));
        actions.andExpect(jsonPath("$.state").value("CA"));
        actions.andExpect(jsonPath("$.bp").doesNotExist());

    }

    @Test
    public void testCreateCityFail() throws Exception {
        JSONObject city = new JSONObject();
        city.put("name", "city");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());

        //send only state
        city = new JSONObject();
        city.put("state", "city");
        str = city.toJSONString();
        actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());
    }

    @Test
    public void testDeleteCity() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s", city.getId())), currentUser));
        actions.andExpect(status().isOk());
        Assert.assertNull(cityDAO.findOne(city.getId()));
    }

    @Test
    public void testDeleteCityUnknownId() throws Exception {
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s", "123")), currentUser));
        actions.andExpect(status().isInternalServerError());
    }

    @Test
    public void testAddBoardingPoint() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        JSONObject bp = new JSONObject();
        bp.put("name", "BPName");
        bp.put("landmark", "landmark");
        bp.put("contact", "123");
        bp.put("active", true);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        actions.andExpect(jsonPath("$.boardingPoints", Matchers.hasSize(1)));
        actions.andExpect(jsonPath("$.boardingPoints[0].name").value("BPName"));
        actions.andExpect(jsonPath("$.boardingPoints[0].landmark").value("landmark"));
        actions.andExpect(jsonPath("$.boardingPoints[0].contact").value("123"));
        actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        City savedCity = cityDAO.findOne(city.getId());
        Assert.assertEquals(2, savedCity.getBoardingPoints().size());
        for(BoardingPoint b : savedCity.getBoardingPoints()) {
            Assert.assertNotNull(b.getId());
        }
    }

    @Test
    public void testAddBoardingPointNoName() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        BoardingPoint bp = new BoardingPoint(null, "landmark", "123", true);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdateBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        bp.setName("NewName");
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        actions.andExpect(jsonPath("$.boardingPoints[0].name").value(bp.getName()));
        Assert.assertNotNull(cityDAO.findOne(city.getId()));
    }

    @Test
    public void testDeleteBoardingPoint() throws Exception{
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s/boardingpoint/%s", city.getId()
                , bp.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").doesNotExist());
        Assert.assertNotNull(cityDAO.findOne(city.getId()));
    }

    @Test
    public void testUpdateCity() throws Exception{
        City city = new City("TestCity", "TestState", true, new ArrayList<>());
        city = cityDAO.save(city);
        city.setName("NewName");
        city.setActive(false);
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/city/%s", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(city)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        City savedCity = cityDAO.findOne(city.getId());
        Assert.assertNotNull(savedCity);
        Assert.assertFalse(savedCity.isActive());
        Assert.assertEquals(city.getName(), savedCity.getName());
    }

}