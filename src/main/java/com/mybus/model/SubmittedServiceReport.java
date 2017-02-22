package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "SubmittedServiceReport")
@Getter
@Setter
public class SubmittedServiceReport extends AbstractDocument  {
    public static final String COLLECTION_NAME = "SubmittedServiceReport";
    private final ServiceStatus status = ServiceStatus.SUBMITTED;
    private String serviceReportId;
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    private String jDate;
    private Set<VehicleStaff> staff;
    private Collection<Booking> bookings;
    private Collection<Expense> expenses;
    private double netCashIncome;
    private double netRedbusIncome;
    private double netOnlineIncome;
    private double netIncome;
    private String verifiedBy;
    private String conductorInfo;
    private int seatsCount;
    public SubmittedServiceReport() {
        this.staff = new HashSet<>();
        this.bookings = new ArrayList<>();
    }

}
