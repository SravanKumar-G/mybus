package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BranchOffice;
import com.mybus.model.City;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
public class BranchOfficeManager {
    private static final Logger logger = LoggerFactory.getLogger(BranchOfficeManager.class);
    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private AgentDAO agentDAO;


    @Autowired
    private MongoQueryDAO mongoQueryDAO;
    public BranchOffice save(BranchOffice branchOffice) {
        List<String> errors = RequiredFieldValidator.validateModel(branchOffice, BranchOffice.class);
        if(errors.isEmpty()) {
            return branchOfficeDAO.save(branchOffice);
        } else {
            throw new BadRequestException("Required data missing ");
        }
    }
    public BranchOffice findOne(String branchOfficeId) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId is required");
        BranchOffice branchOffice = branchOfficeDAO.findOne(branchOfficeId);
        Preconditions.checkNotNull(branchOffice, "No BranchOffice found with id");
        if(branchOffice.getCityId() != null) {
            City city = cityManager.findOne(branchOffice.getCityId());
            if(city != null) {
                branchOffice.getAttributes().put(BranchOffice.CITY_NAME, city.getName());
            }
        }
        if(branchOffice.getManagerId() != null) {
            User user = userManager.findOne(branchOffice.getManagerId());
            if(user != null) {
                branchOffice.getAttributes().put(BranchOffice.MANAGER_NAME, user.getFullName());
            }
        }
        return branchOffice;
    }

    public BranchOffice update(String branchOfficeId, BranchOffice branchOffice) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId can not be null");
        BranchOffice branchOfficeCopy = branchOfficeDAO.findOne(branchOfficeId);
        Preconditions.checkNotNull(branchOfficeCopy, "No branchOffice found with id");
        try {
            branchOfficeCopy.merge(branchOffice, false);
        } catch (Exception e) {
            throw new BadRequestException("Error updating branchOffice");
        }
        return branchOfficeDAO.save(branchOfficeCopy);
    }

    public Page<BranchOffice> find(JSONObject query, final Pageable pageable) {
        if(logger.isDebugEnabled()) {
            logger.debug("Looking up shipments with {0}", query);
        }
        List<BranchOffice> branchOffices = IteratorUtils.toList(mongoQueryDAO.
                getDocuments(BranchOffice.class, BranchOffice.COLLECTION_NAME, null, query, pageable).iterator());
        Map<String, String> cityNames = cityManager.getCityNamesMap();
        Map<String, String> userNames = userManager.getUserNames(false);
        branchOffices.parallelStream().forEach(office -> {
            office.getAttributes().put(BranchOffice.CITY_NAME, cityNames.get(office.getCityId()));
            office.getAttributes().put(BranchOffice.MANAGER_NAME, userNames.get(office.getManagerId()));
        });
        Page<BranchOffice> page = new PageImpl<BranchOffice>(branchOffices, pageable, branchOffices.size());
        return page;
    }

    public long count(JSONObject query) {
        return mongoQueryDAO.count(BranchOffice.class, BranchOffice.COLLECTION_NAME, null, query);
    }
    public void delete(String branchOfficeId) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId can not be null");
        BranchOffice branchOffice = branchOfficeDAO.findOne(branchOfficeId);
        Preconditions.checkNotNull(branchOffice, "No branchOffice found with id");
        branchOfficeDAO.delete(branchOffice);
    }

    public List<BranchOffice> getNames() {
        String[] fields = {"name"};
        List<BranchOffice> offices = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(BranchOffice.class, BranchOffice.COLLECTION_NAME, fields, null, null).iterator());
        return offices;
    }
    public Map<String, String> getNamesMap() {
        List<BranchOffice> offices = getNames();
        Map<String, String> namesMap = new HashMap<>();
        for(BranchOffice office: offices) {
            namesMap.put(office.getId(), office.getName());
        }
        return namesMap;
    }

}
