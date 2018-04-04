package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class CargoBookingItem {
    private String description;
    private String value;
    private String charge;
    private String quantity;
    private String cdm;
    private String weight;
    private int index;
}