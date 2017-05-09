package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

/**
 * Created by busda001 on 4/28/17.
 */

@ToString
@ApiModel(value = "OfficeExpense")
@Getter
@Setter

public class OfficeExpense extends AbstractDocument {
    private Date date;
    private String name;
    private String description;
    private double amount;
    private String branchOfficeId;
    private String status;
    private String vehicleId;
}