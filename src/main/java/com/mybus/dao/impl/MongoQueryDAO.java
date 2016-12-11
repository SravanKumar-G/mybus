package com.mybus.dao.impl;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Shipment;
import com.mybus.util.ServiceUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * A DAO implementation for querying subset of fields from database
 *
 * Created by skandula on 2/13/16.
 */
@Repository
public class MongoQueryDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     *
     * @param className
     * @param collectionName
     * @param fields
     * @param queryInfo
     * @param pageable
     * @return
     */
    public Iterable getDocuments(final Class className, String collectionName, final String[] fields,
                                    final JSONObject queryInfo, final Pageable pageable) {
        /*Preconditions.checkArgument(mongoTemplate.collectionExists(collectionName),
                new BadRequestException("No collection found with name " + collectionName));*/
        Query query = new Query();
        if (fields != null) {
            for(String field :fields){
                query.fields().include(field);
            }
        }
        if (queryInfo != null) {
            for(Object key:queryInfo.keySet()) {

                if(key.toString().equals("dispatchDate")) {
                    List<Criteria> match = new ArrayList<>();
                    String dateValue = queryInfo.get(key.toString()).toString();
                    Date start = null;
                    try {
                        start = Shipment.dateTimeFormat.parse(dateValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    query.addCriteria(where(key.toString()).gte(start));
                } else {
                    query.addCriteria(where(key.toString()).is(queryInfo.get(key.toString()).toString()));
                }

            }
        }
        if (pageable != null) {
            query.skip(pageable.getOffset());
            query.limit(pageable.getPageSize());
            DBObject sort = query.getSortObject();
            //sort.
        }
        return mongoTemplate.find(query, className, collectionName);
    }

    /**
     *
     * @param collectionName
     * @param fields
     * @param queryInfo
     * @param pageable
     * @return
     */
    public Iterable getDocuments(String collectionName, final String[] fields,
                                 final JSONObject queryInfo, final Pageable pageable) {
        return getDocuments(BasicDBObject.class, collectionName, fields, queryInfo, pageable);
    }

    private void createTimeFrameQuery(String key, Date start, Date end, List<Criteria> criteria) {
        if (start == null && end == null) {
            // No timeframe specified, so search over everything
            return;
        } else if (end == null) {
            criteria.add(where(key).gte(start));
        } else if (start == null) {
            criteria.add(where(key).lte(end));
        } else {
            criteria.add(where(key).gte(start).lte(end));
        }
    }
}
