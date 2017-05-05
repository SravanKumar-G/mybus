package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skandula on 2/10/16.
 */

public class MyBusBaseController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleAppException(HttpServletResponse resp, Exception ex) throws IOException {
        ex.printStackTrace();
        //LOG.error("EXCEPTION", mpe);
        resp.setStatus(500); // for now treating all membership profile exceptions the same
        resp.setContentType("application/json");
        Map<String, Object> error = new HashMap<>();
        error.put("code", ex.getMessage());
        resp.getWriter().println( getJsonObject(ex).toJSONString());
    }

    private JSONObject getJsonObject(Exception ex) {
        JSONObject error = new JSONObject();
        error.put("message", ex.getMessage());
        return error;
    }
}

