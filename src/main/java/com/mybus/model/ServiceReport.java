package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceReport")
@Getter
@Setter
public class ServiceReport extends AbstractDocument  {
    public static final String COLLECTION_NAME = "serviceReport";
    private String serviceName;
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    private Date journeyDate;
    private String jDate;
    private Set<VehicleStaff> staff;
    private Collection<Booking> bookings;
    private Set<Expense> expenses;
    private double netCashIncome;
    private double netRedbusIncome;
    private double netOnlineIncome;
    private double netIncome;
    private String submittedBy;
    private String verifiedBy;
    private ServiceStatus status;
    private String conductorInfo;
    public ServiceReport() {
        this.staff = new HashSet<>();
    }

}
