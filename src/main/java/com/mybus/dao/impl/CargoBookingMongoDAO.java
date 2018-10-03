package com.mybus.dao.impl;


import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.mybus.dto.BranchCargoBookingsSummary;
import com.mybus.dto.BranchwiseCargoBookingSummary;
import com.mybus.model.*;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by srinikandula on 12/11/16.
 */
@Repository
public class CargoBookingMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    /**
     * Assign vehicle to cargo bookings
     * @param ids
     * @param vehicleId
     * @param operatorId
     * @return
     */
    public boolean assignVehicles(List<String> ids, String vehicleId, String operatorId) {
        Update updateOp = new Update();
        updateOp.set("vehicleId", vehicleId);
        final Query query = new Query();
        query.addCriteria(where("_id").in(ids));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(operatorId));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, CargoBooking.class);
        return writeResult.getN() == ids.size();
    }

    public List<CargoBooking> findShipments(JSONObject query, final Pageable pageable) throws ParseException {
        final Query q = createSearchQuery(query);
        if(pageable != null) {
            q.with(pageable);
        }
        List<CargoBooking> cargoBookings = mongoTemplate.find(q, CargoBooking.class);
        return cargoBookings;
    }

    private Query createSearchQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query != null) {
            if(query.get("filter") != null) {
                q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(query.get("filter").toString(), "i"));
            }
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("fromBranchId") != null) {
                match.add(Criteria.where("fromBranchId").is(query.get("fromBranchId").toString()));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            if(query.get("deliveredBy") != null) {
                match.add(Criteria.where("deliveredBy").is(query.get("deliveredBy").toString()));
            }
            if(query.get("bookedBy") != null) {
                match.add(Criteria.where("createdBy").is(query.get("bookedBy").toString()));
            }
            if(query.get("paymentType") != null) {
                match.add(Criteria.where("paymentType").is(query.get("paymentType").toString()));
            }
            match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

            if(match.size() > 0) {
                criteria.andOperator(match.toArray(new Criteria[match.size()]));
                q.addCriteria(criteria);
            }
        }
        return q;
    }

    /**
     * Count the shipments
     * @param query
     * @return
     * @throws ParseException
     */
    public long countShipments(JSONObject query) throws ParseException {
        final Query q = createSearchQuery(query);
        return mongoTemplate.count(q, CargoBooking.class);
    }

    /**
     * Get booking summary for one branch office
     * @param branchOfficeId
     * @param start
     * @param end
     * @return
     */
    public BranchwiseCargoBookingSummary getBranchwiseBookingSummary(String branchOfficeId, Date start, Date end) {
        BranchwiseCargoBookingSummary branchwiseCargoBookingSummary  = new BranchwiseCargoBookingSummary();
        //db.cargoBooking.aggregate({$group:{_id:"$paymentType",total:{$sum:"$totalCharge"}, count: { $sum: 1 }}})
        if(start == null || end == null){
            throw new IllegalArgumentException("Dates are required");
        }
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId == null){
            throw new IllegalArgumentException("BranchOfficeId is required");
        }
        match.add(where("fromBranchId").is(branchOfficeId));
        match.add(where("createdAt").gte(start));
        match.add(where("createdAt").lte(end));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("paymentType").sum("totalCharge").as("totalCharge").count().as("totalCount"),
                sort(Sort.Direction.DESC, "totalCharge"));

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, CargoBooking.class, BasicDBObject.class);
        List<BasicDBObject> results = groupResults.getMappedResults();
        for(BasicDBObject result: results){
            BranchCargoBookingsSummary bookingsSummary = new BranchCargoBookingsSummary();
            bookingsSummary.setBranchOfficeId(branchOfficeId);
            if(result.get("_id").toString().equalsIgnoreCase(PaymentStatus.PAID.toString())){
                bookingsSummary.setPaidBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setPaidBookingsCount(result.getInt("totalCount"));
            } else if(result.get("_id").toString().equalsIgnoreCase(PaymentStatus.TOPAY.toString())){
                bookingsSummary.setTopayBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setTopayBookingsCount(result.getInt("totalCount"));
            } else if(result.get("_id").toString().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())){
                bookingsSummary.setPaidBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setPaidBookingsCount(result.getInt("totalCount"));
            }
            branchwiseCargoBookingSummary.getBranchCargoBookings().add(bookingsSummary);
        }

        return branchwiseCargoBookingSummary;
    }

    public BranchwiseCargoBookingSummary getAllBranchsBookingSummary(Date start, Date end) {
        BranchwiseCargoBookingSummary branchwiseCargoBookingSummary  = new BranchwiseCargoBookingSummary();
        //db.cargoBooking.aggregate({$group:{_id:"$paymentType",total:{$sum:"$totalCharge"}, count: { $sum: 1 }}})
        if(start == null || end == null){
            throw new IllegalArgumentException("Dates are required");
        }
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("createdAt").gte(start));
        match.add(where("createdAt").lte(end));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("paymentType", "fromBranchId").sum("totalCharge").as("totalCharge").count().as("totalCount"),
                sort(Sort.Direction.DESC, "totalCharge"));

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, CargoBooking.class, BasicDBObject.class);
        List<BasicDBObject> results = groupResults.getMappedResults();
        for(BasicDBObject result: results){
            BranchCargoBookingsSummary bookingsSummary = new BranchCargoBookingsSummary();
            bookingsSummary.setBranchOfficeId(result.get("fromBranchId").toString());
            if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.PAID.toString())){
                bookingsSummary.setPaidBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setPaidBookingsCount(result.getInt("totalCount"));
            } else if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.TOPAY.toString())){
                bookingsSummary.setTopayBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setTopayBookingsCount(result.getInt("totalCount"));
            } else if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())){
                bookingsSummary.setPaidBookingsTotal(result.getDouble("totalCharge"));
                bookingsSummary.setPaidBookingsCount(result.getInt("totalCount"));
            }
            branchwiseCargoBookingSummary.getBranchCargoBookings().add(bookingsSummary);
        }
        return branchwiseCargoBookingSummary;
    }

    /**
     * Find cargo bookings with a given matching string
     * @param query
     * @return
     */
    public Iterable<CargoBooking> findShipments(JSONObject query){
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query != null) {
            if(query.get("filter") != null) {
                q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(query.get("filter").toString(), "i"));
            }
            match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return mongoTemplate.find(q, CargoBooking.class);
    }

    /**
     * Find cargo bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForUnloading(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            match.add(Criteria.where("cargoTransitStatus").nin(CargoTransitStatus.CANCELLED.getKey(),
                    CargoTransitStatus.ARRIVED.toString(),
                    CargoTransitStatus.DELIVERED.toString(),
                    CargoTransitStatus.ONHOLD.toString()));
            match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return mongoTemplate.find(q, CargoBooking.class);
    }

    public boolean unloadBookings(List<String> bookingIds) {
        User currentUser = sessionManager.getCurrentUser();
        Update updateOp = new Update();
        updateOp.set("cargoTransitStatus", CargoTransitStatus.ARRIVED.toString());
        updateOp.push("messages", "Unloaded by "+ currentUser.getFullName() + " on "+ new Date());
        final Query query = new Query();
        query.addCriteria(where("_id").in(bookingIds));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, CargoBooking.class);
        return writeResult.getN() == bookingIds.size();
    }

    public List<CargoBooking> findUndeliveredShipments(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            match.add(Criteria.where("cargoTransitStatus").nin(CargoTransitStatus.CANCELLED.getKey(),
                    CargoTransitStatus.DELIVERED.toString(),
                    CargoTransitStatus.ONHOLD.toString()));
            match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return mongoTemplate.find(q, CargoBooking.class);
    }
}
