package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by schanda on 01/14/16.
 */
@ToString
@ApiModel(value = "Berth")
@AllArgsConstructor
@NoArgsConstructor
public class Berth extends Seat{

    @Getter
    @Setter
    private BerthPosition position;
    
    @Getter
    @Setter
    private boolean sideSleeper;
    
    
}
