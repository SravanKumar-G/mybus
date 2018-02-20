package com.mybus.model;

import com.google.gson.Gson;
import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = CargoBooking.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class CargoBooking extends AbstractDocument implements AttributesDocument{
    public static final String COLLECTION_NAME="cargoBooking";
    public static final String DISPATCH_DATE="dispatchDate";

    private String forUser;

    @RequiresValue
    @Indexed(unique = true)
    private String shipmentNumber;

    @RequiresValue
    @Indexed(unique = true)
    private String fromBranchId;

    @RequiresValue
    @Indexed(unique = true)
    private String toBranchId;

    @RequiresValue
    private ShipmentStatus shipmentStatus;

    @RequiresValue
    private ShipmentType shipmentType;


    @RequiresValue
    private int noOfPackages;
    private boolean active;

    private String fromEmail;
    @RequiresValue
    @Indexed
    private long fromContact;
    private String fromName;

    private String wayBillNo;
    private String tinNumber;
    private String toEmail;

    @RequiresValue
    @Indexed
    private long toContact;
    private String toName;

    private long weight;
    private String contents;
    private long frightCharges;
    private long deliveryCharges;
    private long otherCharges;

    @RequiresValue
    private long totalCharge;

    @Field(value = DISPATCH_DATE)
    @RequiresValue
    private Date dispatchDate;

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    public static CargoBooking fromJson(String json) {
        return new Gson().fromJson(json, CargoBooking.class);
    }

}
