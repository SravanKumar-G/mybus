package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 
 * @author yks-Srinivas
 *
 */

@ToString
@ApiModel(value = "TripReport")
@Getter
@Setter
public class FuelLoadEntry extends AbstractDocument {
    private String tripReportId;
    private String fillingStationId;
    private double quantity;
    private double rate;
    private double cost;

}
