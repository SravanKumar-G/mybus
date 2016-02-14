package com.mybus.dao.impl;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mybus.exception.BadRequestException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A DAO implementation for querying subset of fields from database
 *
 * Created by skandula on 2/13/16.
 */
@Repository
public class MongoQueryDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Iterable<BasicDBObject> getDocuments(final String collectionName, final String[] fields,
                                                final JSONObject queryInfo, final Pageable pageable) {
        Preconditions.checkArgument(mongoTemplate.collectionExists(collectionName),
                new BadRequestException("No collection found with name " + collectionName));
        Query query = new Query();
        if(fields != null) {
            for(String field :fields){
                query.fields().include(field);
            }
        }
        return mongoTemplate.find(query, BasicDBObject.class, collectionName);
    }
}
