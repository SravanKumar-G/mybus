package com.mybus.model;

/**
 * Created by srinikandula on 2/21/17.
 */
public enum PaymentType {
    CASH,
    ONLINE,
    REDBUS;

    public static PaymentType findByValue(String abbr){
        for(PaymentType v : values()){
            if( v.toString().equals(abbr)){
                return v;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name();
    }
}
