package com.mybus.model;

import lombok.*;
import org.jsondoc.core.annotation.ApiObject;

/**
 * Created by skandula on 12/9/15.
 */
@ToString
@EqualsAndHashCode(callSuper = false, of = { "id" })
@ApiObject(name = "BoardingPoint")
@AllArgsConstructor
@NoArgsConstructor
public class BoardingPoint extends AbstractDocument {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String landmark;

    @Getter
    @Setter
    private String contact;
}
