package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.UserDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BranchOffice;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(CityManager.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    public User findOne(String userId) {
        return userDAO.findOne(userId);
    }
    public User saveUser(User user){
        user.validate();
        User duplicateUser = userDAO.findOneByUserName(user.getUserName());
        if (duplicateUser != null && !duplicateUser.getId().equals(user.getId())) {
            throw new RuntimeException("A user already exists with username");
        }
        BranchOffice office = branchOfficeManager.findOne(user.getBranchOfficeId());
        user.getAttributes().put(BranchOffice.KEY_NAME, office.getName());
        //validateAgent(user);
        if(logger.isDebugEnabled()) {
            logger.debug("Saving user: [{}]", user);
        }
        return userDAO.save(user);
    }

    public User updateUser(User user) {
        Preconditions.checkNotNull(user, "The user can not be null");
        Preconditions.checkNotNull(user.getId(), "Unknown user for update");
        //validateAgent(user);
        User loadedUser = userDAO.findOne(user.getId());
        try {
            loadedUser.merge(user);
        }catch (Exception e) {
            logger.error("Error merging user", e);
            throw new BadRequestException("Error merging user info");
        }
        return saveUser(loadedUser);
    }

    public boolean deleteUser(String userId){
        Preconditions.checkNotNull(userId, "The user id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting user:[{}]" + userId);
        }
        if (userDAO.findOne(userId) != null) {
            userDAO.delete(userId);
        } else {
            throw new RuntimeException("Unknown user id");
        }
        return true;
    }

    public User getUser(String id){
        Preconditions.checkNotNull(id,"UserId cannot be Null");
        User user = userDAO.findOne(id);
        if(user == null){
            throw new RuntimeException("User does not exist with that Id");
        }
        return user;
    }

    /**
     *  Get user names as a map
     * @param includeInactive
     * @return
     */
    public Map<String, String> getUserNames(boolean includeInactive) {
        List<User> users = getUserNamesAsUserList(includeInactive);
        Map<String, String> map = users.stream().collect(
                Collectors.toMap(User::getId, user -> user.getFullName()));
        return map;
    }

    /**
     * Get users as a list only include the name fields
     * @param includeInactive
     * @return
     */
    public List<User> getUserNamesAsUserList(boolean includeInactive) {
        String fields[] = {User.FIRST_NAME, User.LAST_NAME};
        JSONObject query = new JSONObject();
        if(!includeInactive) {
            query.put("active", true);
        }
        List<User> users = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(User.class, User.COLLECTION_NAME, fields, query, null).iterator());
        return users;
    }

    public List<User> findAll() {
        List<User> users = IteratorUtils.toList(userDAO.findAll().iterator());
        /*
        Map<String, String> cityNames = cityManager.getCityNamesMap();
        for(User user:users) {
            user.getAttributes().put("cityName", cityNames.get(user.getCity()));
        }*/
        return users;
    }

    public List<User> getUserCashBalances() {
        String fields[] = {User.FIRST_NAME, User.LAST_NAME, User.AMOUNT_TO_BE_PAID,};
        JSONObject query = new JSONObject();
        List<User> users = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(User.class, User.COLLECTION_NAME, fields, query, null).iterator());
        return users;
    }
/*
    private void validateAgent(User user){
        if (user.getUserType().equals(UserType.AGENT)) {
            Preconditions.checkNotNull(user.getPlanType(), "Agent planType is required");
            Preconditions.checkNotNull(user.getAddress1(),"Agent address required");
            Preconditions.checkNotNull(user.getContact(),"Agent contact number required");
            Preconditions.checkNotNull(user.getCity(),"Agent city required");
            Preconditions.checkNotNull(user.getState(),"Agent state required");
            if (!(user.getPlanType().equals(planTypeDAO.findOneById(user.getPlanType())))) {
                throw new RuntimeException("Plan Does not exist");
            }
        }
    }
    */
}
