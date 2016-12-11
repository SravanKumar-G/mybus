package com.mybus.model;

import lombok.Getter;

/**
 * Created by skandula on 2/13/16.
 */
public enum PaymentStatus {
    TOPAY("To Pay"),
    CREDIT("Credit"),
    PAID("Paid"),
    FREE("Free");
    @Getter
    private final String key;

    PaymentStatus(final String key) {
        this.key = key;
    }

    public static PaymentStatus findByValue(String abbr){
        for(PaymentStatus v : values()){
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
