package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.List;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "Invoice")
@Getter
@Setter
public class Invoice extends AbstractDocument{
    private List<Booking> bookings;
    public Invoice() {

    }

}
