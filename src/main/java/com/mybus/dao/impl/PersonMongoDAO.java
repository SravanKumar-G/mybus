package com.mybus.dao.impl;

import com.mybus.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class PersonMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void updatePhone(String name, long phone) {
        Update updateOp = new Update();
        updateOp.set("phone", phone);
        final Query query = new Query();
        query.addCriteria(where("name").is(name));
        mongoTemplate.updateMulti(query, updateOp, Person.class);
    }
}
