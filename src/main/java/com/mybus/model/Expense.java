package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "Expense")
public class Expense extends AbstractDocument implements AttributesDocument{

    @Getter
    @Setter
    private String date;
    
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private ExpenseType type;

    @Getter
    @Setter
    private double amount;


    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
}
