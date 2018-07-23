package com.mybus.service;

import com.mybus.dao.StaffDAO;
import com.mybus.dao.impl.StaffMongoDAO;
import com.mybus.model.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class StaffManager {
    private static final Logger logger = LoggerFactory.getLogger(StaffManager.class);

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private StaffMongoDAO staffMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    public Staff saveStaff(Staff staff){
        staff.validate();
        Staff savedStaff = staffDAO.findOneByName(staff.getName());
        if (savedStaff != null && !savedStaff.getId().equals(staff.getId())) {
            throw new RuntimeException("A Staff already exists with the same name");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Saving staff: [{}]", staff);
        }
        staff.setOperatorId(sessionManager.getOperatorId());
        return staffDAO.save(staff);
    }

    public long count(String filter, Pageable pageable) {
        return staffMongoDAO.count(filter);
    }
    public Page<Staff> findStaff(String filter, Pageable pageable) {
        return staffMongoDAO.getStaff(filter, pageable);
    }
}
