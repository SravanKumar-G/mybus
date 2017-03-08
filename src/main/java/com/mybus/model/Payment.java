package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "Payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends AbstractDocument {
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    public static final String BRANCHOFFICEID = "branchOfficeId";

    private Date date;
    private String dateStr;
    private String name;
    private int index;
    private String description;
    private PaymentType type;
    private double amount;
    private String formId;
    private String branchOfficeId;
    private String status;
}
