package com.mybus.dao.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import com.mybus.service.SessionManager;

/**
 * Created by schanda on 1/16/16.
 */
@Repository
public class LayoutMongoDAO {
    
    @Autowired
    private LayoutDAO layoutDAO;
    
    @Autowired
    private SessionManager sessionManager;

    public Layout save(Layout layout){
    	layout.setCreatedAt(new DateTime());
        layout.setCreatedBy(sessionManager.getCurrentUser().getFirstName());
        return layoutDAO.save(layout);
    }
    public Layout update(Layout layout){
        layout.setUpdatedAt(new DateTime());
        layout.setUpdatedBy(sessionManager.getCurrentUser().getFirstName());
        return layoutDAO.save(layout);
    }

}
