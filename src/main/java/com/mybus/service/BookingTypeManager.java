package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.City;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public static boolean isRedbusBooking(Booking booking) {
        return booking.getBookedBy().equalsIgnoreCase("REDBUS-API");
    }
    public static boolean isOnlineBooking(Booking booking) {
        if(booking.getBookedBy().equalsIgnoreCase("ONLINE") ||
                booking.getBookedBy().equalsIgnoreCase("YATRAGENIE-API") ||
                booking.getBookedBy().equalsIgnoreCase("PAYTM-API") ||
                booking.getBookedBy().equalsIgnoreCase("ABHIBUS") ||
                booking.getBookedBy().equalsIgnoreCase("")){
            return true;
        } else {
            return false;
        }
    }
}
