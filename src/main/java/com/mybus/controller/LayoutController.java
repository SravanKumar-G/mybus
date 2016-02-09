package com.mybus.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import com.mybus.model.LayoutType;
import com.mybus.service.LayoutManager;

@Controller
@RequestMapping(value = "/api/v1/")
@Api(value = "LayoutController", description = "Management of the seats of a Bus")
public class LayoutController {

	private static final Logger logger = LoggerFactory.getLogger(LayoutController.class);

	@Autowired
	private LayoutDAO layoutDAO;

	@Autowired
	private LayoutManager layoutManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "layouts", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get all the layouts available", response = Layout.class, responseContainer = "List")
	public Iterable<Layout> getLayouts(HttpServletRequest request) {
		return layoutDAO.findAll();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "layout", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Create a layout", response = Layout.class)
	public Layout createLayout(HttpServletRequest request,
			@ApiParam(value = "JSON for Layout to be created") @RequestBody final Layout layout) {
		logger.debug("post layout called");
		return layoutManager.saveLayout(layout);
	}

	@RequestMapping(value = "layout/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get the Layout JSON", response = Layout.class)
	public Layout getLayout(HttpServletRequest request,
			@ApiParam(value = "Id of the Layout to be found") @PathVariable final String id) {
		logger.debug("get layout called");
		return layoutDAO.findOne(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "layout/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a layout")
	public JSONObject deleteLayout(HttpServletRequest request,
			@ApiParam(value = "Id of the layout to be deleted") @PathVariable final String id) {
		logger.debug("get layout called");
		JSONObject response = new JSONObject();
		response.put("deleted", layoutManager.deleteLayout(id));
		return response;
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "layout", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Update a layout", response = Layout.class)
	public Layout updateLayout(HttpServletRequest request,
			@ApiParam(value = "JSON for layout") @RequestBody final Layout layout) {
		logger.debug("update layout called");
		return layoutManager.updateLayout(layout);
	}

	@RequestMapping(value = "layout/default/{layoutType}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get the default Layout JSON", response = Layout.class)
	public Layout getDefaultLayout(HttpServletRequest request,
			@ApiParam(value = "default layout for the layoutType") @PathVariable final String layoutType) {
		logger.debug("get default layout called for input " + layoutType);
		LayoutType lt = LayoutType.valueOf(layoutType);
		logger.debug(" layout type : " + layoutType);
		return layoutManager.getDefaultLayout(lt);
	}

}
