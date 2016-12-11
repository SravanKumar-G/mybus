package com.mybus.model;

import com.google.gson.Gson;
import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = Shipment.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class Shipment extends AbstractDocument implements AttributesDocument{
    public static final String COLLECTION_NAME="shipment";
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

    @RequiresValue
    private String fromCityId;
    @RequiresValue
    private String toCityId;
    @RequiresValue
    private PaymentStatus paymentStatus;
    @RequiresValue
    private ShipmentStatus shipmentStatus;
    @RequiresValue
    private int noOfPackages;
    private boolean active;
    @RequiresValue
    private String email;
    @RequiresValue
    private long fromContact;
    @RequiresValue
    private long toContact;
    @RequiresValue
    private String fromNameAddress;
    @RequiresValue
    private String toNameAddress;
    private long weight;
    private String contents;
    private long frightCharges;
    private long deliveryCharges;
    private long otherCharges;
    @RequiresValue
    private long totalCharge;

    @RequiresValue
    private Date dispatchDate;

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    public static Shipment fromJson(String json) {
        return new Gson().fromJson(json, Shipment.class);
    }

}
