package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "Expense")
@Getter
@Setter
@NoArgsConstructor
public class Expense extends AbstractDocument {
    private String date;
    private String name;
    private int index;
    private String description;
    private ExpenseType type;
    private double amount;
    private String serviceId;
}
