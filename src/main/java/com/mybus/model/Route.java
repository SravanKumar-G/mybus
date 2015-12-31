package com.mybus.model;

import lombok.*;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiObject(name = "Route")
@AllArgsConstructor
@NoArgsConstructor
public class Route extends AbstractDocument{

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String fromCity;

    @Getter
    @Setter
    private String toCity;

    @Getter
    @Setter
    @ApiObjectField(description = "Ordered list of cityIds through which the bus can operate")
    private Set<String> viaCities = new LinkedHashSet<>();

    @Getter
    @Setter
    private boolean active = true;
}
