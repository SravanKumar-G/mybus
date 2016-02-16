package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.User;
import com.mybus.model.Vehicle;
import com.mybus.service.VehicleManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/api/v1/")
public class VehicleController extends MyBusBaseController{
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private VehicleManager vehicleManager;


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicles", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the vehicles available", response = Vehicle.class, responseContainer = "List")
    public Iterable<Vehicle> getVehicles(HttpServletRequest request) {
    	return vehicleDAO.findAll();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value ="Get vehicle", response = User.class)
    public Vehicle getVehicle(HttpServletRequest request,
                                     @ApiParam(value = "Id of the vehicle to be found") @PathVariable final String id
                                    ) {
        logger.debug("get vehicle called");
        return vehicleDAO.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Create a new vehicle")
    public ResponseEntity createVehicle(HttpServletRequest request,
                    @ApiParam(value = "JSON for Vehicle to be created") @RequestBody final Vehicle vehicle){
        logger.debug("create vehicle called");
        return new ResponseEntity<>(vehicleManager.saveVehicle(vehicle), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value ="Update vehicle", response = User.class)
    public ResponseEntity updateVehicle(HttpServletRequest request,
                                     @ApiParam(value = "Vehicle JSON") @RequestBody final Vehicle vehicle) {
        logger.debug("update vehicle called");
        return new ResponseEntity<>(vehicleManager.updateVehicle(vehicle), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value ="Delete a vehicle")
    public JSONObject deleteVehicle(HttpServletRequest request,
                                 @ApiParam(value = "Id of the vehicle to be deleted") @PathVariable final String id) {
        logger.debug("delete vehicle called");
        JSONObject response = new JSONObject();
        response.put("deleted", vehicleManager.deleteVehicle(id));
        return response;
    }
}
