package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FillingStation extends AbstractDocument {
    @RequiresValue
    private String name;
    private String address;
    private boolean active;
}
