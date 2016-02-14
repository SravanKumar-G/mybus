package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.UserDAO;
import com.mybus.model.City;
import com.mybus.model.User;
import com.mybus.service.UserManager;
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
import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/")
public class UserController extends MyBusBaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserManager userManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/me", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public User getUserInfo(HttpServletRequest request) {
        User account = (User)userDAO.findOneByUserName(request.getUserPrincipal().getName());
        return account;
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/groups", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public List<String> getUserGroups(HttpServletRequest request) {
        return null;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "users", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the users available", response = User.class, responseContainer = "List")
    public Iterable<User> getUsers(HttpServletRequest request) {
        return userDAO.findAll();
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Create a user")
    public ResponseEntity createUser(HttpServletRequest request,
                                     @ApiParam(value = "JSON for User to be created") @RequestBody final User user){
        logger.debug("create user called");
        return new ResponseEntity<>(userManager.saveUser(user), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value ="Update user", response = User.class)
    public ResponseEntity updateUser(HttpServletRequest request,
                                     @ApiParam(value = "Id of the User to be found") @PathVariable final String id,
                                     @ApiParam(value = "User JSON") @RequestBody final User user) {
        logger.debug("update user called");
        return new ResponseEntity<>(userManager.updateUser(user), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value ="Delete a user")
    public JSONObject deleteUser(HttpServletRequest request,
                                 @ApiParam(value = "Id of the user to be deleted") @PathVariable final String id) {
        logger.debug("delete use called");
        JSONObject response = new JSONObject();
        response.put("deleted", userManager.deleteUser(id));
        return response;
    }
}
