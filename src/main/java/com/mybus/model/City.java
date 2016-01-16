package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "City")
@AllArgsConstructor
@NoArgsConstructor
@Document
public class City extends AbstractDocument{
    private static final String KEY_NAME = "name";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_STATE = "state";
    private static final String KEY_BOARDING_POINTS = "bp";

    @Getter
    @Setter
    @Field(KEY_NAME)
    @ApiModelProperty
    private String name;

    @Getter
    @Setter
    @Field(KEY_STATE)
    @ApiModelProperty
    private String state;

    @Getter
    @Setter
    @Field(KEY_ACTIVE)
    @ApiModelProperty
    private boolean active;

    @Getter
    @Setter
    @Field(KEY_BOARDING_POINTS)
    @ApiModelProperty
    private Set<BoardingPoint> boardingPoints = new HashSet<>();

 }
