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
import java.util.List;


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
    public static final String SHIPMENT_NUMBER="shipmentNumber";

    private String forUser;

    @RequiresValue
    @Field(SHIPMENT_NUMBER)
    @Indexed(unique = true)
    private String shipmentNumber;

    @RequiresValue
    @Indexed(unique = true)
    private String fromBranchId;

    @RequiresValue
    @Indexed(unique = true)
    private String toBranchId;

    private boolean copySenderDetails;

    @RequiresValue
    private ShipmentStatus shipmentStatus = ShipmentStatus.READYFORSHIPMENT;
    @RequiresValue
    private PaymentStatus paymentStatus;

    @RequiresValue
    private String shipmentType;

    private String fromEmail;

    @Indexed
    @RequiresValue
    private Long fromContact;
    private String fromName;

    private String wayBillNo;
    private String tinNumber;
    private String toEmail;

    @Indexed
    @RequiresValue
    private Long toContact;
    private String toName;

    private long loadingCharge = 0;
    private long unloadingCharge = 0;
    private long otherCharge = 0;


    @RequiresValue
    private long totalCharge;

    @Field(value = DISPATCH_DATE)
    @RequiresValue
    private Date dispatchDate;

    private String bookedBy;
    private String deliveredBy;
    private String loadedBy;

    private List<CargoBookingItem> items;

    private String remarks;

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    public static CargoBooking fromJson(String json) {
        return new Gson().fromJson(json, CargoBooking.class);
    }
}

