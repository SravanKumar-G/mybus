package com.mybus.model;

import com.google.gson.Gson;
import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.SimpleDateFormat;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = BranchOffice.COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BranchOffice extends AbstractDocument implements AttributesDocument{
    public static final String COLLECTION_NAME="branchOffice";
    public static final String CITY_NAME="cityName";
    public static final String MANAGER_NAME="managerName";
    public static final String KEY_NAME = "branchName";

    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Field(KEY_NAME)
    @RequiresValue
    private String name;

    @RequiresValue
    private String cityId;
    private String managerId;

    @RequiresValue
    private boolean active;

    private String email;
    @RequiresValue
    private long contact;

    @RequiresValue
    private String address;

    private double cashBalance = 0;

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    public static BranchOffice fromJson(String json) {
        return new Gson().fromJson(json, BranchOffice.class);
    }

}
