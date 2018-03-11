package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.User;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class UserMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean updateCashBalance(String userId, double cashBalance) {
        Update updateOp = new Update();
        updateOp.inc("amountToBePaid", cashBalance);
        final Query query = new Query();
        query.addCriteria(where("_id").is(userId));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, User.class);
        return writeResult.getModifiedCount() == 1;
    }

    public boolean updatePassword(String userName, String password) {
        Update updateOp = new Update();
        updateOp.set("password", password);
        final Query query = new Query();
        query.addCriteria(where("userName").is(userName));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, User.class);
        return writeResult.getModifiedCount() == 1;
    }

    public Map<String, List<User>> getUsersByBranchOffices() {
        //db.booking.aggregate([{ $match: { 'due': true } },{$group:{_id:"$serviceNumber",total:{$sum:"$netAmt"}}}])
        /*
        Aggregation agg = newAggregation(
                match(Criteria.where("due").is(true)),
                group("hosting").count().as("total"),
                project("total").and("hosting").previousOperation(),
                sort(Sort.Direction.DESC, "total");
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                match.add(where("bookedBy").in(agentNames));
            }
        }
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("due").is(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("serviceNumber").sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, BasicDBObject.class);
        List<BasicDBObject> result = groupResults.getMappedResults();
        return result;*/
        return null;
    }


}
