package com.mybus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsondoc.core.annotation.ApiObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiObject(name = "User")
public class User extends AbstractDocument implements AttributesDocument{
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private boolean admin;
    @Getter
    @Setter
    private boolean active;



    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }



}
