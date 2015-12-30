package com.mybus.model;

import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.jsondoc.core.annotation.ApiObject;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by skandula on 12/9/15.
 */
@ToString
@EqualsAndHashCode(callSuper = false, of = { "id" })
@NoArgsConstructor
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
    public BoardingPoint(String name, String landmark, String contact) {
        setId(new ObjectId().toString());
        this.name = name;
        this.landmark = landmark;
        this.contact = contact;
    }
    public void merge(BoardingPoint bp) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(this, bp);
    }
}
