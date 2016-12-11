package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Shipment;
import com.mybus.service.ShipmentManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="ShipmentController", description="ShipmentController management APIs")
public class ShipmentController extends MyBusBaseController{

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    private ShipmentManager shipmentManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipments", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the shipments available", response = Shipment.class, responseContainer = "List")
    public Iterable<Shipment> getAll(HttpServletRequest request,
                                     @ApiParam(value = "JSON Query") @RequestBody(required = false) final JSONObject query,
                                     final Pageable pageable) {
        return shipmentManager.findShipments(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a Shipment")
    public Shipment create(HttpServletRequest request,
                        @ApiParam(value = "JSON for Shipment to be created") @RequestBody final Shipment shipment) {
        logger.debug("save shipment called");
        return shipmentManager.saveWithValidations(shipment);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a Shipment")
    public Shipment update(HttpServletRequest request,
                           @ApiParam(value = "JSON for Shipment to be updated") @RequestBody final Shipment shipment) {
        logger.debug("update shipment called");
        return shipmentManager.saveWithValidations(shipment);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Shipment", response = Shipment.class)
    public Shipment get(HttpServletRequest request,
                     @ApiParam(value = "Id of the Shipment to be found") @PathVariable final String id) {
        logger.debug("get shipment called");
        return shipmentManager.findOne(id);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a Shipment", response = Shipment.class)
    public JSONObject delete(HttpServletRequest request,
                        @ApiParam(value = "Id of the Shipment to be removed") @PathVariable final String id) {
        logger.debug("delete shipment called");
        JSONObject response = new JSONObject();
        shipmentManager.delete(id);
        response.put("deleted", true);
        return response;
    }
}
