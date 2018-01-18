package com.mybus.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.Invoice;
import com.mybus.service.BookingTypeManager;
import com.mybus.service.SessionManager;
import org.scribe.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class BookingMongoDAO {

    private static final Logger logger = LoggerFactory.getLogger(BookingMongoDAO.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private SessionManager sessionManager;

    /**
     * Find due bookings by agent names and Journey Date
     * @param agentNames
     * @param JDate
     * @return
     */
    public List<Booking> findDueBookings(List<String> agentNames, String JDate) {
        final Query query = new Query();
        //query.fields().include("name");
        query.addCriteria(where("bookedBy").in(agentNames));
        if(JDate != null) {
            query.addCriteria(where("jDate").is(JDate));
        }
        addIsBookingDueConditions(query);
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    /**
     * For a given agent name find the branchOffice and find the due bookings whose source of the journey doesn't match
     * branchoffice city name.
     * @param agent
     * @return
     */
    public List<Booking> findReturnTicketDuesForAgent(Agent agent ) {
        long start = System.currentTimeMillis();
        Preconditions.checkNotNull(agent, "Agent not found");
        BranchOffice branchOffice = branchOfficeDAO.findOne(agent.getBranchOfficeId());
        Preconditions.checkNotNull(branchOffice, "Branchoffice not found");
        final Query query = new Query();
        query.addCriteria(where("bookedBy").is(agent.getUsername()));
        addIsBookingDueConditions(query);
        query.addCriteria(where("source").ne(branchOffice.getName()));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        long end = System.currentTimeMillis();
        logger.info(String.format("Finding return tickets for agent %s took " + (end-start), agent.getUsername()));
        return bookings;
    }

    /**
     * Add the query conditions to check if the booking is due
     * @param query
     */
    private void addIsBookingDueConditions(Query query) {
        query.addCriteria(where("due").is(true));
        query.addCriteria(where("formId").exists(true));
        query.addCriteria(where("serviceId").exists(false));
    }

    public List<Booking> findAgentDues(String agentName) {
        final Query query = new Query();
        //query.fields().include("name");
        query.addCriteria(where("bookedBy").is(agentName));
        addIsBookingDueConditions(query);
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }
    public boolean markBookingPaid(String bookingId) {
        Update updateOp = new Update();
        updateOp.set(Booking.DUE, false);
        updateOp.set(Booking.PAID_BY, sessionManager.getCurrentUser().getId());
        updateOp.set(Booking.PAID_ON, new Date());
        final Query query = new Query();
        query.addCriteria(where("_id").is(bookingId));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Booking.class);
        if(writeResult.getN() != 1) {
            return false;
        }
        return true;
    }
    public List<BasicDBObject> getBookingDueTotalsByService(String branchOfficeId){
        //db.booking.aggregate([{ $match: { 'due': true } },{$group:{_id:"$serviceNumber",total:{$sum:"$netAmt"}}}])
        /*
        Aggregation agg = newAggregation(
                match(Criteria.where("due").is(true)),
                group("hosting").count().as("total"),
                project("total").and("hosting").previousOperation(),
                sort(Sort.Direction.DESC, "total");*/
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                match.add(where("bookedBy").in(agentNames));
            }
        }
        match.add(where("due").is(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("serviceNumber").sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, BasicDBObject.class);
        List<BasicDBObject> result = groupResults.getMappedResults();
        return result;
    }

    public Page<BasicDBObject> getBookingCountsByPhone(Pageable pageable){
        /**
         * db.booking.aggregate(
         [
         {
         $group:
         {
         _id: { phoneNo:  "$phoneNo"},
         count: { $sum: 1 }
         }
         },{
         $sort:{count:1}
         }
         ]
         )
         */

        long total = getTotalDistinctPhoneNumbers();
        Aggregation agg = newAggregation(
                group("phoneNo").count().as("totalBookings"),
                sort(Sort.Direction.DESC, "totalBookings"),
                skip((long)pageable.getPageNumber() * pageable.getPageSize()),
                limit(pageable.getPageSize()));
        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, BasicDBObject.class);
        List<BasicDBObject> result = groupResults.getMappedResults();
        return new PageImpl<>(result, pageable, total);
    }

    public long getTotalDistinctPhoneNumbers() {
        return mongoTemplate.getCollection("booking").distinct("phoneNo").size();
    }
    public List<BasicDBObject> getDueBookingByAgents(String branchOfficeId){
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                match.add(where("bookedBy").in(agentNames));
            }
        }
        match.add(where("due").is(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("bookedBy").sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, BasicDBObject.class);
        List<BasicDBObject> result = groupResults.getMappedResults();
        return result;
    }

    public List<Booking> getDueBookingByServiceNumber(String branchOfficeId, String serviceNumber){
        final Query query = new Query();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                query.addCriteria(where("bookedBy").in(agentNames));
            }
        }
        addIsBookingDueConditions(query);
        query.addCriteria(where("serviceNumber").is(serviceNumber));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    public Invoice findBookingsInvoice(Date start, Date end, List<String> bookedBy) {
        Invoice invoice = new Invoice();
        final Query query = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(bookedBy != null && bookedBy.size() != 0 && bookedBy.size() !=3){
            List<String> channels = new ArrayList<>();
            for(String name: bookedBy) {
                if(name.equals("ABHIBUS")) {
                    channels.addAll(BookingTypeManager.ABHIBUS_BOOKING_CHANNELS);
                }
            }
            match.add(where("bookedBy").in(channels));
        }
        match.add(where("journeyDate").gte(start).lte(end));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        query.addCriteria(criteria);
        invoice.setBookings(mongoTemplate.find(query, Booking.class));
         Aggregation agg = newAggregation(
                match(criteria),
                group().sum("netAmt").as("bookingTotal").sum("serviceTax").as("totalTax"));
        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, BasicDBObject.class);
        List<BasicDBObject> result = groupResults.getMappedResults();
        if(!result.isEmpty()) {
            invoice.setTotalTax(result.get(0).getDouble("totalTax"));
            invoice.setTotalSale(result.get(0).getDouble("bookingTotal"));
        }
        return invoice;
    }



    public List<Booking> findDueBookings(Date start, Date end, List<String> bookedBy) {
        final Query query = new Query();
        if(bookedBy != null && bookedBy.size() != 0){
            query.addCriteria(where("bookedBy").in(bookedBy));
        }
        query.addCriteria(where("due").is(true));
        query.addCriteria(where("journeyDate").gte(start).lte(end));
        query.addCriteria(where("formId").exists(true));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    public Booking findFormBooking(String ticketNumber) {
        final Query query = new Query();
        if(ticketNumber == null ){
            throw new BadRequestException("ticketNumber is required");
        }
        query.addCriteria(where("ticketNo").is(ticketNumber));
        query.addCriteria(where("formId").exists(true));
        Booking booking = mongoTemplate.findOne(query, Booking.class);
        return booking;
    }

}
