package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "Route")
@AllArgsConstructor
@NoArgsConstructor
public class Route extends AbstractDocument{

    @Getter
    @Setter
    @ApiModelProperty
    private String name;

    @Getter
    @Setter
    @ApiModelProperty
    private String fromCity;

    @Getter
    @Setter
    @ApiModelProperty
    private String toCity;

    @Getter
    @Setter
    @ApiModelProperty(value = "Ordered list of cityIds through which the bus can operate")
    private LinkedHashSet<String> viaCities = new LinkedHashSet<>();

    @Getter
    @Setter
    @ApiModelProperty
    private boolean active = true;

}
