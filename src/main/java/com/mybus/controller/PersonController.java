package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.PersonDAO;
import com.mybus.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by skandula on 1/7/16.
 */

@Controller
@RequestMapping(value = "/api/v1/")
public class PersonController {

    @Autowired
    private PersonDAO personDAO;

    @RequestMapping(value = "persons", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public Iterable<Person> getPersons() {
        Iterable<Person> persons = personDAO.findAll();
        return persons;
    }
}
