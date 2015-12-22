package com.mybus.model;

import lombok.*;
import org.jsondoc.core.annotation.ApiObject;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiObject(name = "City")
@AllArgsConstructor
@NoArgsConstructor
public class City extends AbstractDocument{
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "state";
    private static final String KEY_BOARDING_POINTS = "bp";

    @Getter
    @Setter
    @Field(KEY_NAME)
    private String name;

    @Getter
    @Setter
    @Field(KEY_STATE)
    private String state;

    @Getter
    @Setter
    @Field(KEY_BOARDING_POINTS)
    private Set<BoardingPoint> boardingPoints = new HashSet<>();


    public void setName(String name) {
        this.name = name;
    }
 }
