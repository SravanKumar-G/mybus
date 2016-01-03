package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = { "id" })
@ApiModel(value = "AbstractDocument")
public abstract class AbstractDocument {

    public static final String KEY_ID = "_id";

    public static final String KEY_ATTRIBUTES = "attrs";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    @Id @Getter @Setter
    @Field(KEY_ID)
    @ApiModelProperty(value = "ID of the Object")
    private String id;

    @Getter
    @Setter
    @CreatedDate
    @Field(KEY_CREATED_AT)
    @ApiModelProperty(value = "Time at which Object Was Created")
    private DateTime createdAt;

    @Version
    private Long version;

    @Getter
    @Setter
    @LastModifiedDate
    @Field(KEY_UPDATED_AT)
    @ApiModelProperty(value = "Time at which Object Was Last Updated")
    private DateTime updatedAt;

    @Getter
    @Setter
    @CreatedBy
    @ApiModelProperty(value = "User who created the object")
    private String createdBy;

    @Getter
    @Setter
    @LastModifiedBy
    @ApiModelProperty(value = "User who updated the object")
    private String updatedBy;

    @Getter
    @Setter
    @ApiModelProperty(value = "Additional Fields on the Object")
    @JsonProperty(KEY_ATTRIBUTES)
    @Field(KEY_ATTRIBUTES)
    private Map<String, String> attributes = new HashMap<>();

}
