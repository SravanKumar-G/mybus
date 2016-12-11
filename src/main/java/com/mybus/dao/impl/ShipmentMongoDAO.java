package com.mybus.dao.impl;


import com.mongodb.BasicDBObject;
import com.mybus.model.Shipment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srinikandula on 12/11/16.
 */
@Repository
public class ShipmentMongoDAO {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    public Iterable<Shipment> findShipments(JSONObject query, final Pageable pageable) {
        Iterable<Shipment> shipments = mongoQueryDAO.getDocuments(Shipment.class, Shipment.COLLECTION_NAME, null, query, pageable);
        return shipments;
    }
}
