package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by skandula on 5/7/16.
 */
public class Booking extends AbstractDocument{

    @Setter
    @Getter
    private String emailID;

    @Setter
    @Getter
    private String phoneNo;

    @Setter
    @Getter
    private String firstName;

    @Setter
    @Getter
    private String lastName;
    @Setter
    @Getter
    private String gender;

    @Setter
    @Getter
    private String address;

    @Setter
    @Getter
    private String city;

    @Setter
    @Getter
    private String state;

    @Setter
    @Getter
    private String country;

    @Setter
    @Getter
    private String postalCode;
    @Setter
    @Getter
    private String paymentType;

    @Setter
    @Getter
    private float amount;

    @Getter
    @Setter
    private Payment payment;

}
