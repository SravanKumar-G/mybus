package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "City")
@NoArgsConstructor
@Document
public class City extends AbstractDocument{
    private static final String KEY_NAME = "name";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_STATE = "state";
    private static final String KEY_SHORT_CODE = "sc";
    private static final String KEY_LANG_CODE = "langCode";
    private static final String KEY_BOARDING_POINTS = "bp";
    private static final String KEY_IS_HUB = "hub";


    @Getter
    @Setter
    @Field(KEY_SHORT_CODE)
    @ApiModelProperty(dataType = "int")
    private String shortCode;

    @Getter
    @Setter
    @Field(KEY_LANG_CODE)
    @ApiModelProperty
    private String langCode;


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
    private boolean active = true;

    @Getter
    @Setter
    @Field(KEY_IS_HUB)
    @ApiModelProperty
    private boolean hub = true;

    @Getter
    @Setter
    @Field(KEY_BOARDING_POINTS)
    @ApiModelProperty
    private List<BoardingPoint> boardingPoints = new ArrayList<>();

    public City(String name, String state, boolean active, List<BoardingPoint> bps) {
        this.name = name;
        this.state = state;
        this.active = active;
        this.boardingPoints = bps;
    }

    public City(String name, String state, boolean active,boolean hub, List<BoardingPoint> bps) {
        this.name = name;
        this.state = state;
        this.active = active;
        this.boardingPoints = bps;
        this.hub = hub;
    }

 }
