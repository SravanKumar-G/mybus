package com.mybus.dao.impl;

import com.mybus.model.Staff;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long count(String filter) {
        Query q = createQuery(filter);
        return mongoTemplate.count(q, Staff.class);
    }

    private Query createQuery(String filter) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(filter != null && !filter.equals("null")) {
            match.add(Criteria.where("name").regex(filter, "i"));
            match.add(Criteria.where("contactNumber").regex(filter, "i"));
            match.add(Criteria.where("aadharNumber").regex(filter, "i"));
            criteria.orOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        if(sessionManager.getOperatorId() != null){
            q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        }
        return q;
    }
    public Page<Staff> getStaff(String filter, Pageable pageable){
        Query q = createQuery(filter);
        if(pageable != null) {
            q.with(pageable);
        }
        List<Staff> staff = mongoTemplate.find(q, Staff.class);
        staff.stream().forEach(s -> {
            s.setNameCode(String.format("%s (%s)", s.getName(), s.getCode()));
        });
        return new PageImpl<Staff>(staff);
    }



}
