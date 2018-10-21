package com.mybus.service;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.model.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

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

    @Autowired
    private UserManager userManager;

    @Autowired
    private SessionManager sessionManager;

    /**
     * Get the due report for all the branch offices, will be used by admin
     * @return
     */
    public List<BranchOfficeDue> getBranchOfficesDueReports() {
        List<BranchOffice> offices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        List<BranchOfficeDue> responses = new ArrayList<>();
        offices.stream().forEach(office -> {
            responses.add(getBranchOfficeDueReport(office, false, null));
        });
        return responses;
     }

    /**
     *  Get the due report for a branch office including it's due bookings.
     * @param office
     * @param includeBookings - false for creating admin due report
     * @return
     */
    public BranchOfficeDue getBranchOfficeDueReport(BranchOffice office, boolean includeBookings, String jDate) {
        logger.info("Preparing due report");
        BranchOfficeDue officeDue = new BranchOfficeDue();
        officeDue.setName(office.getName());
        officeDue.setBranchOfficeId(office.getId());
        officeDue.setCashBalance(office.getCashBalance());
        officeDue.setManagerName(office.getAttributes().get(BranchOffice.MANAGER_NAME));
        List<String> namesList = agentMongoDAO.findAgentNamesByOfficeId(office.getId());
        List<Booking> bookings = bookingMongoDAO.findDueBookings(namesList, jDate);
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

    public List<Booking> getBranchOfficeDues(BranchOffice office) {
        logger.info("Preparing due report for office "+ office.getName());
        BranchOfficeDue officeDue = new BranchOfficeDue();
        officeDue.setName(office.getName());
        officeDue.setBranchOfficeId(office.getId());
        officeDue.setCashBalance(office.getCashBalance());
        officeDue.setManagerName(office.getAttributes().get(BranchOffice.MANAGER_NAME));
        List<String> namesList = agentMongoDAO.findAgentNamesByOfficeId(office.getId());
        List<Booking> bookings = bookingMongoDAO.findDueBookings(namesList, null);
        return bookings;
    }

    /**
     * Find branch office due report by it's id, include the bookings as well
     * @param branchOfficeId
     * @return
     */
    public BranchOfficeDue getBranchOfficeDueReport(String branchOfficeId) {
        BranchOffice office = branchOfficeManager.findOne(branchOfficeId);
        return getBranchOfficeDueReport(office, true, null);
    }

    public List<Booking> getBranchOfficeDues(String branchOfficeId) {
        BranchOffice office = branchOfficeManager.findOne(branchOfficeId);
        return getBranchOfficeDues(office);
    }
    /**
     * Find office dues group by Journey date
     * @param branchOfficeId
     * @return
     */
    public BranchOfficeDue findOfficeDuesGroupByDate(String branchOfficeId) {
        BranchOfficeDue officeDue = new BranchOfficeDue();
        BranchOffice office = branchOfficeManager.findOne(branchOfficeId);
        officeDue.setName(office.getName());
        officeDue.setBranchOfficeId(office.getId());
        officeDue.setCashBalance(office.getCashBalance());
        officeDue.setManagerName(office.getAttributes().get(BranchOffice.MANAGER_NAME));
        List<String> namesList = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
        List<Booking> bookings = bookingMongoDAO.findDueBookings(namesList, null);
        class OfficeDueByDate implements Comparable {
            @Getter
            @Setter
            private String date;
            @Getter
            @Setter
            private double totalDue;
            public OfficeDueByDate(String date) {
                this.date = date;
            }

            @Override
            public int compareTo(Object o) {
                return ((OfficeDueByDate)o).getDate().compareTo(this.getDate());
            }
        }
        Map<String, OfficeDueByDate> officeDues = new HashMap<>();
        for(Booking booking: bookings) {
            if(officeDues.get(booking.getJDate()) == null) {
                officeDues.put(booking.getJDate(), new OfficeDueByDate(booking.getJDate()));
            }
            OfficeDueByDate dueByDate = officeDues.get(booking.getJDate());
            dueByDate.setTotalDue(dueByDate.getTotalDue() + booking.getNetAmt());
        }
        List result = Lists.newArrayList(officeDues.values());
        Collections.sort(result);
        officeDue.setDuesByDate(result);
        return officeDue;
    }

    public BranchOfficeDue getOfficeDuesByDate(String officeId, String jDate) {
        BranchOffice office = branchOfficeManager.findOne(officeId);
        return getBranchOfficeDueReport(office, true, jDate);
    }

    public List<BasicDBObject> getOfficeDuesByService() {
        User currentUser = sessionManager.getCurrentUser();
        if(currentUser != null && !currentUser.isAdmin()) {
            return bookingMongoDAO.getBookingDueTotalsByService(currentUser.getBranchOfficeId());
        } else {
            return bookingMongoDAO.getBookingDueTotalsByService(null);
        }
    }


    public List<Booking> getDueBookingsByService(String serviceNumber) {
        User currentUser = sessionManager.getCurrentUser();
        if(currentUser != null && !currentUser.isAdmin()) {
            return bookingMongoDAO.getDueBookingByServiceNumber(currentUser.getBranchOfficeId(), serviceNumber);
        } else {
            return bookingMongoDAO.getDueBookingByServiceNumber(null, serviceNumber);
        }
    }

    public List<Booking> getDueBookingsByAgent(String agentName) {
        return bookingMongoDAO.findAgentDues(agentName);
    }

    public List<BasicDBObject> getBookingDuesGroupByAgent() {
        User currentUser = sessionManager.getCurrentUser();
        if(currentUser != null && !currentUser.isAdmin()) {
            return bookingMongoDAO.getDueBookingByAgents(currentUser.getBranchOfficeId());
        } else {
            return bookingMongoDAO.getDueBookingByAgents(null);
        }
    }

    /**
     * Find bookings that doesn't have source same the agent's branch office city.
     * @param branchOfficeId -- if branchOfficeId is specified it returns bookingDues only for that
     * @return
     */
    public List<Booking> getReturnTicketDues(String branchOfficeId) {
        List<Agent> agents = agentMongoDAO.findAgents(null, false);
        List<Booking> bookings = new ArrayList<>();
        agents.parallelStream().forEach(agent -> {
            //if branchoffice doesn't match skip the agent
            if(branchOfficeId != null && agent.getBranchOfficeId() != null){
                if(agent.getBranchOfficeId().equals(branchOfficeId)) {
                    bookings.addAll(bookingMongoDAO.findReturnTicketDuesForAgent(agent));
                }
            } else {
                if(agent.getBranchOfficeId() != null) {
                    bookings.addAll(bookingMongoDAO.findReturnTicketDuesForAgent(agent));
                }
            }
        });
        return bookings;
    }

    public void groupReturnTicketDues(List<Booking> bookings, Map<Long, List<Booking>> byDate, Map<String, List<Booking>> byAgentName){

        bookings.stream().forEach(booking -> {
            Long bookingDate = Long.valueOf(booking.getJourneyDate().getTime());
            if(byDate.get(bookingDate) == null){
                byDate.put(bookingDate, new ArrayList<Booking>());
            }
            byDate.get(bookingDate).add(booking);
            if(byAgentName.get(booking.getBookedBy()) == null){
                byAgentName.put(booking.getBookedBy(), new ArrayList<Booking>());
            }
            byAgentName.get(booking.getBookedBy()).add(booking);
        });

    }
    public Map<String, List<Booking>> groupReturnTicketDuesByAgentName(List<Booking> bookings){
        Map<String, List<Booking>> result = new HashMap<>();
        bookings.parallelStream().forEach(booking -> {
            if(result.get(booking.getBookedBy()) == null){
                result.put(booking.getBookedBy(), new ArrayList<Booking>());
            }
            result.get(booking.getBookedBy()).add(booking);
        });
        return result;
    }

    public List<Booking> searchDues(String start, String end, String branchOfficeId) throws ParseException {
        List<String> namesList = null;
        if(branchOfficeId != null) {
            namesList = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
        }
        List<Booking> dues = bookingMongoDAO.findDueBookings(ServiceConstants.parseDate(start), ServiceConstants.parseDate(end),
                namesList);
        return dues;
    }

    public Iterable<Booking> searchDuesByPNR(String pnr) {
        List<Booking> bookings = new ArrayList<>();
        Booking booking = bookingMongoDAO.findFormBooking(pnr);
        if(booking != null && booking.getPaidBy() != null){
            booking.getAttributes().put(Booking.PAID_BY, userManager.getUser(booking.getPaidBy()).getFullName());
        }
        bookings.add(booking);
        return bookings;
    }
}
