package com.mybus.model;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
@ApiModel(value = "Role")
public class Role extends AbstractDocument {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Set<String> menus;

    public Role(final String roleName,Set<String> menus) {
        this.name = roleName;
        this.menus = menus;
     }
    
    public Role(final String roleName) {
       this.name = roleName;
    }
    
    public Role() {

    }
}