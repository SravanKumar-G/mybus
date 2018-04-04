package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.OperatorAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.List;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
@SessionScope
public class BookingTypeManager {
    private static final Logger logger = LoggerFactory.getLogger(BookingTypeManager.class);
    public static final String REDBUS_CHANNEL = "REDBUS-API";
    public static final String ONLINE_CHANNEL = "ONLINE";
    public static final String CASH_CHANNEL = "CASH";
    public static final String BITLA_BUS = "Bitla";
    public static final String Abhi_BUS = "Abhibus";

    public static final List<String> ABHIBUS_BOOKING_CHANNELS = Arrays.asList("ONLINE", "YATRAGENIE-API", "PAYTM-API", "ABHIBUS");

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    private OperatorAccount operatorAccount;

    public boolean isRedbusBooking(Booking booking) {
        if(operatorAccount == null) {
            operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        }
        if(booking.getBookedBy() == null) {
            return false;
        }
        return booking.getBookedBy().equalsIgnoreCase(REDBUS_CHANNEL);
    }

    public boolean isRedbusBooking(Booking booking, String providerType) {
        if(operatorAccount == null) {
            operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        }
        if(booking.getBookedBy() == null) {
            return false;
        }
        if(providerType.equalsIgnoreCase(BITLA_BUS)){
            return booking.getBookingType().equalsIgnoreCase("4");
        } else {
            return booking.getBookedBy().equalsIgnoreCase(REDBUS_CHANNEL);
        }

    }
    public boolean isOnlineBooking(Booking booking) {
        if(operatorAccount == null) {
            operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        }
        if(booking.getBookedBy() == null) {
            return false;
        }
        if(ABHIBUS_BOOKING_CHANNELS.contains(booking.getBookedBy())){
            return true;
        } else {
            return false;
        }
    }

    public boolean isOnlineBooking(Booking booking, String providerType) {
        if(operatorAccount == null) {
            operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        }
        if(booking.getBookingType() == null) {
            return false;
        }
        if(providerType.equalsIgnoreCase(BITLA_BUS)){
            return operatorAccount.getOnlineBookingTypes().indexOf(booking.getBookingType()) != -1;
        } else {
            if(ABHIBUS_BOOKING_CHANNELS.contains(booking.getBookedBy())){
                return true;
            } else {
                return false;
            }
        }

    }
    public boolean hasValidAgent(Booking booking) {
        if(isOnlineBooking(booking)) {
            return true;
        }
        return getBookingAgent(booking) != null;
    }

    /**
     * Finds booking agent also checks if the agent has a valid branchoffice allocated to it.
     * @param booking
     * @return
     */
    public Agent getBookingAgent(Booking booking) {
        if(booking.getBookedBy() == null) {
            return null;
        }
        Agent agent = agentDAO.findByUsername(booking.getBookedBy());
        if(agent != null) {
            if(agent.getBranchOfficeId() != null) {
                BranchOffice branchOffice = branchOfficeDAO.findOne(agent.getBranchOfficeId());
                if(branchOffice != null) {
                    return agent;
                }
            }
        }
        return null;
    }
}
