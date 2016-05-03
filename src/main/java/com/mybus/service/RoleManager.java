package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.RoleDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Role;
import com.mybus.model.User;
import com.mybus.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
@Service
public class RoleManager {
    private static final Logger logger = LoggerFactory.getLogger(RoleManager.class);

    @Autowired
    private RoleDAO roleDAO;

    public Role saveRole(Role role) {
            //role.id =123, role.name=test
        //1234,test
            Preconditions.checkNotNull(role, "The role can not be null");
            Preconditions.checkNotNull(role.getName(), "role name can not be null");
            Role duplicateRole = (Role) roleDAO.findOneByName(role.getName());
            //duplicateRole =123, name=test

            if (duplicateRole != null && !duplicateRole.getId().equals(role.getId())) {
                throw new RuntimeException("Role already exists with this same name");
            }
            if(logger.isDebugEnabled()) {
                logger.debug("Saving role: [{}]", role);
            }
        return roleDAO.save(role);
    }


    public Role updateRole(Role role) {
        //id = 1234, name =test

        Preconditions.checkNotNull(role, "The role can not be null");
        Preconditions.checkNotNull(role.getId(), "Unknown role for update");

        Role loadedRole = roleDAO.findOne(role.getId());//1234,test , 2nd obj test

        if(loadedRole != null && loadedRole.getName().equals(role.getName())) {
            throw new RuntimeException("Role already exists with this same name in DB");
            //if (loadedRole != null && loadedRole.getName().equals(role.getName())) {
        } else{
                try {
                    saveRole(loadedRole);
                    saveRole(role);
                    loadedRole.merge(role);
                } catch (Exception e) {
                    logger.error("Error merging role", e);
                    throw new BadRequestException("Error merging role info");
                }
            }
        //}
        /*else{
            throw new RuntimeException("Role already exists with this same name");
        }*/

        return saveRole(loadedRole);
    }


    public boolean deleteRole(String roleId) {

        Preconditions.checkNotNull(roleId, "The role id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting role:[{}]" + roleId);
        }
        if (roleDAO.findOne(roleId) != null) {
            roleDAO.delete(roleId);
        } else {
            throw new RuntimeException("Unknown role id");
        }
        return true;
    }


    public Role getRole(String roleId){
        Preconditions.checkNotNull(roleId, "The role id can not be null");
        Role role =  roleDAO.findOne(roleId);
        return role;
    }


}
