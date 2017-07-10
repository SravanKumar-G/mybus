package com.mybus.model.cargo;

import com.mybus.model.AbstractDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by busda001 on 7/8/17.
 */
@Getter
@Setter
public class ShipmentSequence extends AbstractDocument{
    @Indexed(unique = true)
    public String shipmentCode;
    public String shipmentType;
    public long startNumber;
    public long nextNumber;
}
