package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(booking.getBookedBy() == null) {
            return false;
        }
        return booking.getBookedBy().equalsIgnoreCase("REDBUS-API");
    }
    public boolean isOnlineBooking(Booking booking) {
        if(booking.getBookedBy() == null) {
            return false;
        }
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
