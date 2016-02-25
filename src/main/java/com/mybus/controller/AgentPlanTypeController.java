package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.AgentPlanTypeDAO;
import com.mybus.model.AgentPlanType;
import com.mybus.service.AgentPlanTypeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by svanik on 2/23/2016.
 */

@Controller
@RequestMapping(value = "/api/v1/")
@Api(value="AgentPlanTypeController", description="AgentPlanTypeController management APIs")
public class AgentPlanTypeController extends MyBusBaseController{
    private static final Logger logger = LoggerFactory.getLogger(AgentPlanTypeController.class);

    @Autowired
    private AgentPlanTypeDAO agentPlanTypeDAO;

    @Autowired
    private AgentPlanTypeManager agentPlanTypeManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plans", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the plans available", response = AgentPlanType.class, responseContainer = "List")
    public Iterable<AgentPlanType> getAll(HttpServletRequest request) {
        return agentPlanTypeDAO.findAll();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plan", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Create a PlanType")
    public AgentPlanType create(HttpServletRequest request,
                        @ApiParam(value = "JSON for Plan Type to be created") @RequestBody final AgentPlanType agentPlanType) {
        logger.debug("save plan type called");
        return agentPlanTypeManager.saveAgentPlanType(agentPlanType);
    }
}
