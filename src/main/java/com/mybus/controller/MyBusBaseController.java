package com.mybus.controller;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by skandula on 2/10/16.
 */
public class MyBusBaseController {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public JSONObject handleAppException(RuntimeException ex) {
        JSONObject error = new JSONObject();
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JSONObject handleAppException(Exception ex) {
        JSONObject error = new JSONObject();
        error.put("message", ex.getMessage());
        return error;
    }

}

