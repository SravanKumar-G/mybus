package com.mybus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsondoc.core.annotation.ApiObject;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiObject(name = "Expense")
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
