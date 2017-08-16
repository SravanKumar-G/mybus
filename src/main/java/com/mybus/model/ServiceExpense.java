package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;


@ToString
@ApiModel(value = "ServiceExpense")
@Getter
@Setter
@CompoundIndex(name = "service_expenses_jdate_srvnum", def = "{'serviceNumber' : 1, 'journeyDate' : 1}", unique = true)
/**
 * Model for saving Daily service expenses. Data for this will be partially sourced from ServiceExpenseMaster
 */
public class ServiceExpense extends AbstractDocument  {

    public static final String COLLECTION_NAME = "serviceExpense";
    public static final String SUBMITTED_ID = "formId";
    public static final String JOURNEY_DATE = "journeyDate";
    /**
     * The auto generated id of the ServiceReport
     */
    @RequiresValue
    @Indexed
    private String serviceReportId;

    @RequiresValue
    @Indexed
    private String serviceNumber;

    @RequiresValue
    @Indexed
    private Date journeyDate;
    private double avgFuelConsumption;
    private double fuelConsumption;
    private double taxPayment;
    private double financeEMI;
    private double avgMaintenanace;

    public ServiceExpense(ServiceReport serviceReport) {
        if(serviceReport.getId() == null) {
            throw new RuntimeException("Only saved service report can be used to create service expense");
        }
        this.serviceReportId = serviceReport.getId();
        this.journeyDate = serviceReport.getJourneyDate();
    }
}
