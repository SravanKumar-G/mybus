package com.mybus.service;

import com.google.common.base.Preconditions;
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

    public User saveUser(User user){
        Preconditions.checkNotNull(user, "The user can not be null");
        Preconditions.checkNotNull(user.getUserName(), "Username can not be null");
        Preconditions.checkNotNull(user.getFirstName(), "User firstname can not be null");
        Preconditions.checkNotNull(user.getLastName(), "User lastname can not be null");
        Preconditions.checkNotNull(user.getEmail(), "User emails can not be null");
        Preconditions.checkNotNull(user.getContact(), "User contact can not be null");
        Preconditions.checkNotNull(user.getPassword(), "User password can not be null");
        Preconditions.checkNotNull(user.getType(), "User type can not be null");
        User duplicateUser = userDAO.findOneByUserName(user.getUserName());

        if (duplicateUser != null && !duplicateUser.getId().equals(user.getId())) {
            throw new RuntimeException("A user already exists with username");
        }
        if (user.getType().equals(UserType.AGENT)) {
            Preconditions.checkNotNull(user.getCommission(), "Agent commision is required");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Saving user: [{}]", user);
        }
        return userDAO.save(user);
    }

    public User updateUser(User user) {
        Preconditions.checkNotNull(user, "The user can not be null");
        Preconditions.checkNotNull(user.getId(), "Unknown user for update");
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
}
