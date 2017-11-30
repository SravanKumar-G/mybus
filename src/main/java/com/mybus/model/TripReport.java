package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@ToString
@ApiModel(value = "TripReport")
@Getter
@Setter
public class TripReport extends AbstractDocument {
    private Date startDate;
    private String startServiceReportId;
    private Date endDate;
    private String endServiceReportId;

    private String vehicleRegNumber;

    private double startAdvance;
    private String startAdvanceGivenBy;
    private double extraAdvance;
    private String extraAdvanceGivenBy;
    private List<FuelLoadEntry> fuelLoadEntries;
    private List<String> staffIds;
    private List<Payment> expenses;
    private String remarks;
    private String status;

}
