package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.UserDAO;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/")
public class UserController extends MyBusBaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserManager userManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/me", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public User getUserInfo(HttpServletRequest request) {
        User account = (User)userDAO.findOneByUserName(request.getUserPrincipal().getName());
        return account;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/count", method = RequestMethod.GET)
    public long getUserCount(HttpServletRequest request) {
        return userDAO.count();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/groups", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public List<String> getUserGroups(HttpServletRequest request) {
        return null;
    }

    @RequestMapping(value = "users", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the users available", response = User.class, responseContainer = "List")
    public List<User> getUsers(HttpServletRequest request)
    {
        return userManager.findAll();
    }

    @RequestMapping(value = "userNames", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get user names ", response = User.class, responseContainer = "List")
    public List<User> getUserNames(HttpServletRequest request,
                                               @RequestParam(value ="activeOnly", required = false) boolean includeInactive) {
        return userManager.getUserNamesAsUserList(includeInactive);
    }

    @RequestMapping(value = "userNamesMap", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get user names ", response = User.class, responseContainer = "List")
    public Map<String,String> getUserNamesMap(HttpServletRequest request,
                                   @RequestParam(value ="activeOnly", required = false) boolean includeInactive) {
        return userManager.getUserNames(includeInactive);
    }
    @RequestMapping(value = "user", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a user")
    public User createUser(HttpServletRequest request,
                                     @ApiParam(value = "JSON for User to be created") @RequestBody JSONObject userJson){
        logger.debug("create user called");
        return userManager.saveUser(new User(userJson));
    }

    @RequestMapping(value = "userEdit/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="Update user", response = User.class)
    public User updateUser(HttpServletRequest request,
                                     @ApiParam(value = "Id of the User to be found")
                                     @PathVariable final String id,
                                     @ApiParam(value = "User JSON") @RequestBody JSONObject userJson) {
        logger.debug("update user called");
        User user = new User(userJson);
        user.setId(id);
        return userManager.updateUser(user);
    }

    @RequestMapping(value = "userId/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get user with Id", response = User.class)
    public User getUser(HttpServletRequest request,
                                     @ApiParam(value = "Id of the User to be found") @PathVariable final String id) {
        logger.debug("get user called");
        return userManager.getUser(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete a user")
    public JSONObject deleteUser(HttpServletRequest request,
                                 @ApiParam(value = "Id of the user to be deleted") @PathVariable final String id) {
        logger.debug("delete use called");
        JSONObject response = new JSONObject();
        response.put("deleted", userManager.deleteUser(id));
        return response;
    }
}
