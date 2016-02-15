package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "Vehicle")
@NoArgsConstructor
public class Vehicle extends AbstractDocument  {

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String regNo;

    @Getter
    @Setter
    private boolean active;

    public Vehicle(final String type, final String description, final String regNo, final boolean active){
        this.type = type;
        this.description = description;
        this.regNo = regNo;
        this.active = active;
    }
}
