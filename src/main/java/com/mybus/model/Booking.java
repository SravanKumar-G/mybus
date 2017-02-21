package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "Booking")
@Getter
@Setter
public class Booking extends AbstractDocument{
    private String serviceId;
    private String emailID;
    private String name;
    private String phoneNo;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private PaymentType paymentType;
    private float amount;
    private Payment payment;
    private String ticketNo;
    private Date jouurneyDateTime;
    private String seats;
    private String source;
    private String destination;
    private String bookedBy;
    private double basicAmount;
    private double serviceTax;
    private double commission;
    private String boardingPoint;
    private String landmark;
    private String boardingTime;
    private String orderId;
    private double netAmt;
    public Booking() {

    }

}
