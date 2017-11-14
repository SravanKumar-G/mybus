package com.mybus.service;

import com.mongodb.BasicDBObject;
import com.mybus.dao.GSTFilterDAO;
import com.mybus.dao.impl.GSTFilterMongoDAO;
import com.mybus.model.GSTFilter;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class GSTFilterService {

    @Autowired
    private GSTFilterMongoDAO gstFilterMongoDAO;

    @Autowired
    private GSTFilterDAO gstFilterDAO;

    /**
     *
     */
    @Scheduled(fixedDelay = 900000)
    public void refreshGSTFilters(){
        List<String> serviceNumbers = gstFilterMongoDAO.getGSTFilterServiceNumbers();
        List<BasicDBObject> filters = gstFilterMongoDAO.getUniqueServiceNumbers(serviceNumbers);
        filters.stream().filter(filter -> !serviceNumbers.contains(filter.getString("serviceNumber")))
                .forEach(filter -> {
                    if(gstFilterDAO.findOneByServiceNumber(filter.getString("_id")) == null) {
                        gstFilterDAO.save(new GSTFilter(filter.getString("_id"), filter.getString("serviceName")));
                    }
                });
    }

    public List<GSTFilter> getGSTFilters(){
        return IteratorUtils.toList(gstFilterDAO.findAll().iterator());
    }
    public Iterable<GSTFilter> saveGSTFilters(List<GSTFilter> filters) {
        filters.stream().forEach(filter -> {
            GSTFilter gstFilter = gstFilterDAO.findOne(filter.getId());
            gstFilter.setHasGST(filter.isHasGST());
            gstFilterDAO.save(gstFilter);
        });
        return gstFilterDAO.findAll();
    }
}