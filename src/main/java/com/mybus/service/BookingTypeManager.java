package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
public class BookingTypeManager {
    private static final Logger logger = LoggerFactory.getLogger(BookingTypeManager.class);
    public static final String REDBUS_CHANNEL = "REDBUS-API";
    public static final String ONLINE_CHANNEL = "ONLINE";
    public static final String CASH_CHANNEL = "CASH";

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    public boolean isRedbusBooking(Booking booking) {
        return booking.getBookedBy().equalsIgnoreCase("REDBUS-API");
    }
    public boolean isOnlineBooking(Booking booking) {
        if(booking.getBookedBy().equalsIgnoreCase("ONLINE") ||
                booking.getBookedBy().equalsIgnoreCase("YATRAGENIE-API") ||
                booking.getBookedBy().equalsIgnoreCase("PAYTM-API") ||
                booking.getBookedBy().equalsIgnoreCase("ABHIBUS")){
            return true;
        } else {
            return false;
        }
    }

    public boolean hasValidAgent(Booking booking) {
       Agent agent = agentDAO.findByUsername(booking.getBookedBy());
        if(agent != null) {
           if(agent.getBranchOfficeId() != null) {
                BranchOffice branchOffice = branchOfficeDAO.findOne(agent.getBranchOfficeId());
                if(branchOffice != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
