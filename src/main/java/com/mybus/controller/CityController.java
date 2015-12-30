package com.mybus.controller;

import com.mybus.CityManager;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.CityDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/api/v1/")
public class CityController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private CityManager cityManager;
    
    @Autowired
    private SessionManager sessionManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "cities", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiResponseObject
    public Iterable<City> getUserInfo(HttpServletRequest request) {
        return cityDAO.findAll();
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponseObject
    public City createCity(HttpServletRequest request, @ApiBodyObject @RequestBody final City city) {
        logger.debug("post city called");
        return cityManager.saveCity(city);
    }

    @RequestMapping(value = "city/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiResponseObject
    public City getCity(HttpServletRequest request, @PathVariable final String id) {
        logger.debug("get city called");
        return cityDAO.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiResponseObject
    public JSONObject deleteCity(HttpServletRequest request, @PathVariable final String id) {
        logger.debug("get city called");
        JSONObject response = new JSONObject();
        response.put("deleted", cityManager.deleteCity(id));
        return response;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint", method = RequestMethod.POST,
            produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponseObject
    public City createCityBoardingpoint(HttpServletRequest request,
                           @PathVariable final String cityId,
                           @RequestBody final BoardingPoint bp) {
        logger.debug("create boardingpoint called");
        return cityManager.addBoardingPointToCity(cityId, bp);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint", method = RequestMethod.PUT,
            produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponseObject
    public City updateCityBoardingpoint(HttpServletRequest request,
                                        @PathVariable final String cityId,
                                        @RequestBody final BoardingPoint bp) {
        logger.debug("create boardingpoint called");
        return cityManager.updateBoardingPoint(cityId, bp);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint/{id}", method = RequestMethod.DELETE,
            produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public City deleteCityBoardingpoint(HttpServletRequest request,
                                        @PathVariable final String cityId,
                                        @PathVariable final String id) {
        logger.debug("create boardingpoint called");
        return cityManager.deleteBoardingPoint(cityId, id);
    }
}
