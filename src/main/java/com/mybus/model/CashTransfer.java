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
@ApiModel(value = "CashTransfer")
@Getter
@Setter
@NoArgsConstructor
public class CashTransfer extends AbstractDocument {
    public static final String STATUS_AUTO = "Auto";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String FROM_OFFICE_ID = "fromOfficeId";
    public static final String TO_OFFICE_ID = "toOfficeId";

    private Date date;
    private int index;
    private String description;
    private double amount;
    private String fromOfficeId;
    private String toOfficeId;
    private String status;
}
