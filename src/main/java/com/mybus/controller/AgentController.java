package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.ServiceForm;
import com.mybus.model.ServiceReport;
import com.mybus.model.Shipment;
import com.mybus.service.ABAgentService;
import com.mybus.service.AgentManager;
import com.mybus.service.ServiceReportsManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class AgentController {
	private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private AgentManager agentManager;

	@Autowired
	private ABAgentService agentService;

	@Autowired
	private AgentDAO agentDAO;

	@RequestMapping(value = "agent/download", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Download the agents from Abhibus", response = JSONObject.class)
	public JSONObject getDownloadStatus(HttpServletRequest request) {
		JSONObject response = new JSONObject();
		try {
			agentService.downloadAgents();
			response.put("status", "success");
		}catch (Exception e) {
			response.put("status", "fail");
			response.put("error", e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "agent/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get the agents ", response = JSONObject.class)
	public Iterable<Agent> getAllAgents(HttpServletRequest request) {
		return agentManager.findAll();
	}

	@RequestMapping(value = "agent/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get agent ", response = JSONObject.class)
	public Agent getAgent(HttpServletRequest request,
									@ApiParam(value = "Id of the agent to be found") @PathVariable final String id) {
		return agentManager.getAgent(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "agent", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save agent")
	public Agent update(HttpServletRequest request,
						   @ApiParam(value = "JSON for Agent to be updated") @RequestBody final Agent agent) {
		logger.debug("update shipment called");
		return agentManager.save(agent);
	}


}