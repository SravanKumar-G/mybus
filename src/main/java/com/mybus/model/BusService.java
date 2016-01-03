package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.Set;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "BusService")
@AllArgsConstructor
@NoArgsConstructor
public class BusService extends AbstractDocument{
    @Getter
    @Setter
    private boolean active;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String routeId;

    @Getter
    @Setter
    private Set<String> fromCityBoardingpoints;


}
