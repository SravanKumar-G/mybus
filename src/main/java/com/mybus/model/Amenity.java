package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by skandula on 5/7/16.
 */
public class Amenity extends AbstractDocument {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private boolean active;

}
