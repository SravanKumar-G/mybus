package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "Vehicle")
@NoArgsConstructor
@Getter
@Setter
public class Vehicle extends AbstractDocument  {

    @RequiresValue
    private String vehicleType;
    private String description;
    @RequiresValue
    private String regNo;
    private String chasisNumber;
    private String engineNumber;
    private int noOfSeats;
    private int noOfBerths;
    private boolean active;
    private String ownerName;
    private String ownerAddress;

    private long yearOfManufacture;
    private long numberOfTyres;
    private String permitNumber;
    private Date permitExpiry;
    private String insuranceProvider;
    private String policyNumber;
    private Date insuranceExpiry;
    private String fitnessNumber;
    private String fitnessExpiry;
    private Date pollutionExpiry;
    private double emi;
    private int emiDueOn;
    private List<VehicleTaxPayment> taxPayments;

    public Vehicle(final String type, final String description, final String regNo, final boolean active){
        this.vehicleType = type;
        this.description = description;
        this.regNo = regNo;
        this.active = active;
    }
}
