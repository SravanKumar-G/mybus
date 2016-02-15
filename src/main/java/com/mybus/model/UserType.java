package com.mybus.model;

/**
 * Created by skandula on 2/13/16.
 */
public enum UserType {
    USER,
    EMPLOYEE,
    AGENT,
    ADMIN,
    SUPERADMIN;

    @Override
    public String toString() {
        return name();
    }
}
