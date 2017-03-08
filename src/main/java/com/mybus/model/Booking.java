package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "Booking")
@Getter
@Setter
@EqualsAndHashCode(of={"id", "ticketNo"})
public class Booking extends AbstractDocument{

    private String serviceId;
    private int index;
    private String formId;
    private String emailID;
    private String name;
    private String phoneNo;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BookingType paymentType;
    private double amount;
    private BookingPayment payment;
    private String ticketNo;
    private Date journeyDate;
    private String jDate;
    private String seats;
    private int seatsCount;
    private String source;
    private String destination;
    private String bookedBy;
    private String bookedDate;
    private double basicAmount;
    private double serviceTax;
    private double commission;
    private String boardingPoint;
    private String landmark;
    private String boardingTime;
    private String orderId;
    private double netAmt;
    private boolean due;
    private boolean hasValidAgent;
    public Booking() {

    }

}
