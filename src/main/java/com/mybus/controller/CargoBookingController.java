package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.CargoBooking;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.service.CargoBookingManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="ShipmentController", description="ShipmentController management APIs")
public class CargoBookingController extends MyBusBaseController{

    private static final Logger logger = LoggerFactory.getLogger(CargoBookingController.class);

    @Autowired
    private CargoBookingManager cargoBookingManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipments", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the shipments available", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getAll(HttpServletRequest request,
                                         @RequestBody(required = false) final JSONObject query) throws ParseException {
        int page = 0;
        int count = 10;
        Pageable pageable = null;
        if(query != null) {
            if(query.containsKey("page")){
                page = Integer.parseInt(query.get("page").toString());
                page--;
            }
            if(query.containsKey("count")){
                count = Integer.parseInt(query.get("count").toString());
            }
            String sortOn = "createdAt";
            Sort.Direction sortDirection = Sort.Direction.DESC;

            if(query.containsKey("sort")){
                String[] sortParams = query.get("sort").toString().split(",");
                sortOn = sortParams[0];
                if(sortParams[1].equalsIgnoreCase("DESC")){
                    sortDirection = Sort.Direction.DESC;
                } else {
                    sortDirection = Sort.Direction.ASC;
                }
            }
            pageable = new PageRequest(page, count, sortDirection, sortOn);
        }
        return cargoBookingManager.findShipments(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipments/count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get count of the shipments", response = CargoBooking.class, responseContainer = "List")
    public long count(HttpServletRequest request,
                                         @ApiParam(value = "Name") @RequestBody(required = false)
                                         final JSONObject query) throws ParseException {
        return cargoBookingManager.count(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a CargoBooking")
    public CargoBooking create(HttpServletRequest request,
                               @ApiParam(value = "JSON for CargoBooking to be created") @RequestBody final CargoBooking shipment) {
        logger.debug("save shipment called");
        return cargoBookingManager.saveWithValidations(shipment);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a CargoBooking")
    public CargoBooking update(HttpServletRequest request,
                               @ApiParam(value = "Id of the CargoBooking to be found") @PathVariable final String id,
                               @ApiParam(value = "JSON for CargoBooking to be updated") @RequestBody final CargoBooking shipment) {
        logger.debug("update shipment called");
        return cargoBookingManager.updateShipment(id, shipment);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public CargoBooking get(HttpServletRequest request,
                            @ApiParam(value = "Id of the CargoBooking to be found") @PathVariable final String id) {
        logger.debug("get shipment called");
        return cargoBookingManager.findOne(id);
    }

    @RequestMapping(value = "shipment/search/byLR/{LRNumber}", method = RequestMethod.GET,produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value ="Get the CargoBooking by LRNumber", response = String.class)
    public String getBookingByLR(HttpServletRequest request,
                            @ApiParam(value = "Id of the CargoBooking to be found") @PathVariable final String LRNumber) {
        logger.debug("get shipment called");
        return cargoBookingManager.findByLRNumber(LRNumber);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a CargoBooking", response = CargoBooking.class)
    public JSONObject delete(HttpServletRequest request,
                        @ApiParam(value = "Id of the CargoBooking to be removed") @PathVariable final String id) {
        logger.debug("delete shipment called");
        JSONObject response = new JSONObject();
        cargoBookingManager.delete(id);
        response.put("deleted", true);
        return response;
    }

    @RequestMapping(value = "shipment/types", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public Iterable<ShipmentSequence> getShipmentTypes(HttpServletRequest request) {
        logger.debug("get shipment called");
        return cargoBookingManager.getShipmentTypes();
    }

    @RequestMapping(value = "shipment/pay/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Pay CargoBooking", response = CargoBooking.class)
    public boolean payCargoBooking(HttpServletRequest request,@PathVariable final String id ) {
        logger.debug("Pay cargo booking");
        return cargoBookingManager.payCargoBooking(id);
    }
}
