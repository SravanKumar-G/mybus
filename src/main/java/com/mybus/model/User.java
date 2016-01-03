package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = "User")
@NoArgsConstructor
@AllArgsConstructor
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
