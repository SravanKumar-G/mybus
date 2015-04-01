package com.mybus.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = { "id" })
@ApiObject(name = "AbstractDocument", show = false)
public abstract class AbstractDocument {

    public static final String KEY_ID = "_id";

    public static final String KEY_ATTRIBUTES = "attrs";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    @Id @Getter @Setter
    @Field(KEY_ID)
    @ApiObjectField(description = "ID of the Object")
    private String id;

    @Getter
    @Setter
    @CreatedDate
    @Field(KEY_CREATED_AT)
    @ApiObjectField(description = "Time at which Object Was Created")
    private DateTime createdAt;

    @Getter
    @Setter
    @LastModifiedDate
    @Field(KEY_UPDATED_AT)
    @ApiObjectField(description = "Time at which Object Was Last Updated")
    private DateTime updatedAt;

    @Getter
    @Setter
    @ApiObjectField(description = "Additional Fields on the Object")
    @JsonProperty(KEY_ATTRIBUTES)
    @Field(KEY_ATTRIBUTES)
    private Map<String, String> attributes = new HashMap<>();

}
