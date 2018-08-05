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
@ApiModel(value = "Staff")
@NoArgsConstructor
@Getter
@Setter
public class Staff extends AbstractDocument  {
    private String name;
    private String contactNumber;
    private String aadharNumber;
    private Date dlExpiry;
    private String address;
    private String code;
    private String type;
    private boolean terminated;
    private String remarks;
    private String nameCode;
    private boolean active;

    public Staff(String name, String contactNumber, String aadharNumber){
        this.name = name;
        this.contactNumber = contactNumber;
        this.aadharNumber = aadharNumber;
    }

}

