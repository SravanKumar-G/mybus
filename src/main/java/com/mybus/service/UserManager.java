package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.PlanTypeDAO;
import com.mybus.dao.UserDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.User;
import com.mybus.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(CityManager.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PlanTypeDAO planTypeDAO;

    public User saveUser(User user){
        validate(user);
        User duplicateUser = userDAO.findOneByUserName(user.getUserName());
        if (duplicateUser != null && !duplicateUser.getId().equals(user.getId())) {
            throw new RuntimeException("A user already exists with username");
        }
        validateAgent(user);
        if(logger.isDebugEnabled()) {
            logger.debug("Saving user: [{}]", user);
        }
        return userDAO.save(user);
    }

    public User updateUser(User user) {
        System.out.println("In update");
        Preconditions.checkNotNull(user, "The user can not be null");
        System.out.println("user not null");
        System.out.println("uid"+user.getId());
        Preconditions.checkNotNull(user.getId(), "Unknown user for update");
        validateAgent(user);
        User loadedUser = userDAO.findOne(user.getId());
        try {
            loadedUser.merge(user);
        }catch (Exception e) {
            logger.error("Error merging user", e);
            throw new BadRequestException("Error merging user info");
        }
        System.out.println("After merge");
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

    private void validate(final User user){
        Preconditions.checkNotNull(user, "The user can not be null");
        if(user == null){
            throw new NullPointerException("user can not be null");
        }
        Preconditions.checkNotNull(user.getUserName(), "Username can not be null");
        Preconditions.checkNotNull(user.getFirstName(), "User firstname can not be null");
        Preconditions.checkNotNull(user.getLastName(), "User lastname can not be null");
        Preconditions.checkNotNull(user.getEmail(), "User emails can not be null");
        Preconditions.checkNotNull(user.getContact(), "User contact can not be null");
        Preconditions.checkNotNull(user.getPassword(), "User password can not be null");
        Preconditions.checkNotNull(user.getUserType(), "User type can not be null");
    }

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
}
