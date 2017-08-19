package com.mybus.dao.impl;

import com.mongodb.WriteResult;
import com.mybus.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

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
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, User.class);
        return writeResult.getN() == 1;
    }
}
