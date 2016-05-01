package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
@ApiModel(value = "Role")
public class Role extends AbstractDocument {

    @Getter
    @Setter
    private String name;


    public Role(final String roleName) {
       this.name = roleName;

    }

    public Role() {

    }
}
