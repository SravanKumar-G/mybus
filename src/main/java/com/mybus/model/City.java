package com.mybus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsondoc.core.annotation.ApiObject;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiObject(name = "City")
public class City extends AbstractDocument implements AttributesDocument{

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String state;


    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
}
