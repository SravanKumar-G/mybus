package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

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
