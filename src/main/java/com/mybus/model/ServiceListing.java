package com.mybus.model;

import com.mybus.service.ServiceConstants;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceListing")
@Getter
@Setter
@CompoundIndex(name = "sr_jdate_srvnum", def = "{'serviceNumber' : 1, 'journeyDate' : 1}", unique = true)
public class ServiceListing extends AbstractDocument  {

    public static final String JOURNEY_DATE = "journeyDate";
    /**
     * serviceId from Abhibus
     */
    private String serviceId;
    private String serviceName;
    @Indexed
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    @Field(JOURNEY_DATE)
    @Indexed
    private Date journeyDate;

}
