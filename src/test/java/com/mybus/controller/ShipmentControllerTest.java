package com.mybus.controller;

import com.mybus.dao.ShipmentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.*;
import com.mybus.service.BranchOfficeManager;
import com.mybus.service.CityManager;
import com.mybus.service.ShipmentManager;
import com.mybus.test.util.ShipmentTestService;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;

/**
 * Created by srinikandula on 12/11/16.
 */

public class ShipmentControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ShipmentDAO shipmentDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    private MockMvc mockMvc;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Before
    @After
    public void cleanup() {
        shipmentDAO.deleteAll();
        userDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }

    @Test
    public void testGetAll() throws Exception {
        for(int i=0; i<5; i++) {
            shipmentDAO.save(ShipmentTestService.createNew());
        }
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/shipments")), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void testGetAllFromCityId() throws Exception {
        for(int i=0; i<5; i++) {
            shipmentDAO.save(ShipmentTestService.createNew());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromBranchId", "1234");
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void testGetAllByDispathDate() throws Exception {
        for(int i=0; i<5; i++) {
            Shipment shipment = ShipmentTestService.createNew();
            if(i == 1) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) -1);
                shipment.setDispatchDate(cal.getTime());
            }
            shipmentDAO.save(shipment);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dispatchDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void testGetAllByDispathDateRange() throws Exception {
        for(int i=0; i<5; i++) {
            Shipment shipment = ShipmentTestService.createNew();
            if(i == 1) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) -1);
                shipment.setDispatchDate(cal.getTime());
            }
            shipmentDAO.save(shipment);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dispatchDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void testGetAllByBookingDate() throws Exception {
        for(int i=0; i<5; i++) {
            Shipment shipment = ShipmentTestService.createNew();
            if(i == 1) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, cal.get(Calendar.DATE)-3);
                shipment.setDispatchDate(cal.getTime());
            }
            shipmentDAO.save(shipment);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dispatchDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }
    @Test
    public void testCreateShipment() throws Exception {
        BranchOffice fromBranch = new BranchOffice("FromBranch", "1234");
        BranchOffice toBranch = new BranchOffice("ToBranch", "1234");
        fromBranch = branchOfficeManager.save(fromBranch);
        toBranch = branchOfficeManager.save(toBranch);
        Shipment shipment = ShipmentTestService.createNew();
        shipment.setFromBranchId(fromBranch.getId());
        shipment.setToBranchId(toBranch.getId());
        shipment.setShipmentType(ShipmentType.FREE);


        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/shipment").content(getObjectMapper()
                .writeValueAsBytes(shipment)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromBranchId").value(shipment.getFromBranchId()));
        actions.andExpect(jsonPath("$.toBranchId").value(shipment.getToBranchId()));
        List<Shipment> shipments = IteratorUtils.toList(shipmentDAO.findAll().iterator());
        assertEquals(1, shipments.size());
    }


    @Test
    public void testUpdateShipment() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        shipment = shipmentDAO.save(shipment);
        shipment.setFromEmail("newemail@email.com");
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/shipment/"+shipment.getId())
                .content(getObjectMapper().writeValueAsBytes(shipment))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromEmail").value(shipment.getFromEmail()));
        List<Shipment> shipments = IteratorUtils.toList(shipmentDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        assertEquals(shipment.getFromEmail(), shipments.get(0).getFromEmail());
    }

    @Test
    public void testUpdateShipmentUnSetFields() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        shipment = shipmentDAO.save(shipment);
        shipment.setContents(null);
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/shipment/"+shipment.getId())
                .content(getObjectMapper().writeValueAsBytes(shipment))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        List<Shipment> shipments = IteratorUtils.toList(shipmentDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        assertEquals(shipment.getContents(), null);
    }
    @Test
    public void testGetShipment() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        shipment = shipmentDAO.save(shipment);
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/shipment/" + shipment.getId()), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromEmail").value(shipment.getFromEmail()));
    }
    @Test
    public void testDeleteShipment() throws Exception {
        Shipment shipment = ShipmentTestService.createNew();
        shipment = shipmentDAO.save(shipment);
        ResultActions actions = mockMvc.perform(asUser(delete("/api/v1/shipment/" + shipment.getId()), currentUser));
        actions.andExpect(status().isOk());
        List<Shipment> shipments = IteratorUtils.toList(shipmentDAO.findAll().iterator());
        assertEquals(0, shipments.size());
    }
}