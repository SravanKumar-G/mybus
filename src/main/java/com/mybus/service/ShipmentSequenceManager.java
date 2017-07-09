package com.mybus.service;

import com.mongodb.WriteResult;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.model.cargo.ShipmentSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by busda001 on 7/8/17.
 */
@Service
public class ShipmentSequenceManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;

    public ShipmentSequence nextSequeceNumber(String shipmentCode) {
        Update updateOp = new Update();
        updateOp.inc("nextNumber", 1);
        final Query query = new Query();
        query.addCriteria(where("shipmentCode").is(shipmentCode));
        WriteResult writeResult =  mongoTemplate.updateMulti(query, updateOp, ShipmentSequence.class);
        if(writeResult.getN() == 1){
            return shipmentSequenceDAO.findByShipmentCode(shipmentCode);
        } else {
            throw new IllegalStateException("next number failed");
        }
    }

}
