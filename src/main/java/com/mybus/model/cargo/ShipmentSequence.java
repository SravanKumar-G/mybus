package com.mybus.model.cargo;

import com.mybus.model.AbstractDocument;
import com.mybus.model.ShipmentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by busda001 on 7/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class ShipmentSequence extends AbstractDocument{
    public static String PAID_TYPE = "P";
    public static String TOPAY_TYPE = "TP";

    @Indexed(unique = true)
    public String shipmentCode;
    public String shipmentType;
    public long startNumber;
    public long nextNumber;
    public ShipmentSequence(String code, String description){
        this.shipmentCode = code;
        this.shipmentType = description;
        this.startNumber = this.nextNumber = 10000;
    }
}
