package com.mybus.model;

import com.mybus.service.ServiceConstants;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceReport")
@Getter
@Setter
@CompoundIndex(name = "sr_jdate_srvnum", def = "{'serviceNumber' : 1, 'journeyDate' : 1}", unique = true)
public class ServiceReport extends AbstractDocument  {

    public enum ServiceReportStatus {
        HALT,
        SUBMITTED,
        REQUIRE_VERIFICATION;
        @Override
        public String toString() {
            return name();
        }
    }
    public static final String STATUS_HALT = "Halt";
    public static final String STATUS_SUBMIT = "Submitted";
    public static final String COLLECTION_NAME = "serviceReport";
    public static final String SUBMITTED_ID = "formId";
    public static final String JOURNEY_DATE = "journeyDate";
    /**
     * serviceId from Abhibus
     */
    private String serviceId;
    private String serviceName;
    @Indexed
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    @Field(JOURNEY_DATE)
    @Indexed
    private Date journeyDate;
    private Set<VehicleStaff> staff;
    private Collection<Booking> bookings;
    private Collection<Payment> expenses;
    private double netCashIncome;
    private double netRedbusIncome;
    private double netOnlineIncome;
    private double netIncome;
    
    private Date verifiedOn;
    private String verifiedBy;

    private Date submittedOn;
    private String submitedBy;

    private boolean requiresVerification;
    private int totalSeats;
    @Indexed
    private ServiceStatus status;
    private String conductorInfo;
    private String notes;
    private boolean invalid;
    public String jDate;
    private ServiceExpense serviceExpense;

    public ServiceReport() {
        this.staff = new HashSet<>();
        this.expenses = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }
    public String getJDate(){
        if(this.journeyDate != null) {
            return ServiceConstants.df.format(this.getJourneyDate());
        } else {
            return null;
        }

    }


}
