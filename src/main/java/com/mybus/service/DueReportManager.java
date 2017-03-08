package com.mybus.service;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.BranchOfficeDue;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class DueReportManager {
    private static final Logger logger = LoggerFactory.getLogger(DueReportManager.class);

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    /**
     * Get the due report for all the branch offices
     * @return
     */
    public List<BranchOfficeDue> getBranchOfficeDueReports() {
        List<BranchOffice> offices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        List<BranchOfficeDue> responses = new ArrayList<>();
        offices.stream().forEach(office -> {
            responses.add(getBranchOfficeDueReport(office, false));
        });
        return responses;
     }

    /**
     *  Get the due report for a branch office including it's due bookings
     * @param office
     * @param includeBookings
     * @return
     */
    public BranchOfficeDue getBranchOfficeDueReport(BranchOffice office, boolean includeBookings) {
        logger.info("Preparing due report");
        BranchOfficeDue officeDue = new BranchOfficeDue();
        officeDue.setName(office.getName());
        officeDue.setBranchOfficeId(office.getId());
        officeDue.setCashBalance(office.getCashBalance());
        officeDue.setManagerName(office.getAttributes().get(BranchOffice.MANAGER_NAME));
        List<String> namesList = agentMongoDAO.findAgentNamesByOfficeId(office.getId());
        List<Booking> bookings = bookingMongoDAO.findDueBookingsByAgentNames(namesList);
        bookings.stream().forEach(booking -> {
            officeDue.setTotalDue(officeDue.getTotalDue() +booking.getNetAmt());
            if(includeBookings) {
                if(officeDue.getBookings() == null) {
                    officeDue.setBookings(new ArrayList<>());
                }
                officeDue.getBookings().add(booking);
            }
        });
        return officeDue;
    }

    /**
     * Find branch office due report by it's id, include the bookings as well
     * @param branchOfficeId
     * @return
     */
    public BranchOfficeDue getBranchOfficeDueReport(String branchOfficeId) {
        BranchOffice office = branchOfficeManager.findOne(branchOfficeId);
        return getBranchOfficeDueReport(office, true);
    }

}
