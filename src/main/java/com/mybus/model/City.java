package com.mybus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsondoc.core.annotation.ApiObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiObject(name = "City")
public class City extends AbstractDocument{

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String state;

    @Getter
    @Setter
    private Set<BoardingPoint> boardingPoints = new HashSet<>();

 }
