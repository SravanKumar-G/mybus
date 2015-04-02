package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.UserDAO;
import com.mybus.model.User;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/me", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiResponseObject
    public User getUserInfo(HttpServletRequest request) {
        User account = (User)userDAO.findOneByUsername(request.getUserPrincipal().getName());
        return account;
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "user/groups", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiResponseObject
    public List<String> getUserGroups(HttpServletRequest request) {
        return null;
    }
}
