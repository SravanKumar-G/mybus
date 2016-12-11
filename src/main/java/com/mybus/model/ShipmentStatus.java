package com.mybus.model;

import lombok.Getter;

/**
 * Created by skandula on 2/13/16.
 */
public enum ShipmentStatus {
    READYFORSHIPMENT("Ready"),
    INTRANSIT("In Transit"),
    ARRIVED("Arrived"),
    CANCELLED("Cancelled"),
    DELIVERED("Delivered"),
    ONHOLD("On Hold");

    @Getter
    private final String key;

    ShipmentStatus(final String key) {
        this.key = key;
    }

    public static ShipmentStatus findByValue(String abbr){
        for(ShipmentStatus v : values()){
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
