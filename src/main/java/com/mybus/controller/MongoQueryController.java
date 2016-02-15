package com.mybus.controller;

import com.mongodb.BasicDBObject;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.model.Layout;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by skandula on 2/13/16.
 */
@Controller
@RequestMapping(value = "/api/v1/")
@Api(value = "MongoQueryController", description = "Generic Query APIs")
public class MongoQueryController extends MyBusBaseController{

    @Autowired
    private MongoQueryDAO mongoQueryDAO;


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "documents/{collectionName}", method = RequestMethod.GET,
            produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get documents from a colletion with specific fields queried.",
            response = Layout.class, responseContainer = "List")
    public Iterable<BasicDBObject> getDocuments(HttpServletRequest request,
                       @ApiParam(value = "Name of the collection") @PathVariable final String collectionName,
                       @ApiParam(value = "Names of the fields to query")@RequestParam(value = "fields")  String[] fields,
                       @ApiParam(value = "Query") @RequestParam(value = "query", required = false) JSONObject query){
        return mongoQueryDAO.getDocuments(collectionName, fields, query, null);
    }
}
