package com.mybus.model;


/**
 * Created by skandula on 4/1/15.
 */

public enum PaymentType {
    INCOME,
    EXPENSE;

    @Override
    public String toString() {
        return name();
    }
}
