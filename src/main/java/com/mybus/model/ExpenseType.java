package com.mybus.model;

/**
 * Created by skandula on 4/1/15.
 */

public enum ExpenseType {

    SALARY,
    VEHICLEMAINTAINANCE,
    FUEL,
    OTHER,
    TAX,
    INSURANCE,
    GOODSPURCHASE,
    TIRES;

    @Override
    public String toString() {
        return name();
    }
}
