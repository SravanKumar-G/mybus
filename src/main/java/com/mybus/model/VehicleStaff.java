package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "VehicleStaff")
@NoArgsConstructor
@Getter
@Setter
public class VehicleStaff extends AbstractDocument  {
    private String name;
    private long phone;
    private String aadharNumber;
    private Date dlExpiry;
    private String address;
    private StaffDesignation designation;
}

