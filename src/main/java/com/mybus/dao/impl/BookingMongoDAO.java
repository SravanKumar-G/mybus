package com.mybus.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.Payment;
import org.scribe.utils.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class BookingMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private AgentDAO agentDAO;

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
        Preconditions.checkNotNull(agent, "Agent not found");
        BranchOffice branchOffice = branchOfficeDAO.findOne(agent.getBranchOfficeId());
        Preconditions.checkNotNull(branchOffice, "Branchoffice not found");
        final Query query = new Query();
        query.addCriteria(where("bookedBy").is(agent.getUsername()));
        addIsBookingDueConditions(query);
        query.addCriteria(where("source").ne(branchOffice.getName()));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
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
        updateOp.set("due", false);
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
}
