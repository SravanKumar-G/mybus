package com.mybus.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsondoc.core.annotation.ApiObject;

/**
 * Created by skandula on 12/9/15.
 */
@ToString
@EqualsAndHashCode(callSuper = false, of = { "id" })
@ApiObject(name = "BoardingPoint")
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
