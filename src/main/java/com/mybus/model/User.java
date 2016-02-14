package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = "User")
@NoArgsConstructor

public class User extends AbstractDocument implements AttributesDocument{
    @Getter
    @Setter
    private String userName;

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

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String contact;

    @Getter
    @Setter
    private String address1;

    @Getter
    @Setter
    private String address2;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String state;

    @Getter
    @Setter
    private UserType type;

    @Getter
    @Setter
    private double commission;

    @Getter
    @Setter
    private CommissionType commissionType;



    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    public User(final String firstName, final String lastName, final String userName, final String password,
                boolean active, boolean admin){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.active = active;
        this.admin = admin;
    }
    public User(final String firstName, final String lastName, final String userName, final String password,
                final boolean active,
                final boolean admin,
                final String email,
                final String contact,
                final String address1,
                final String address2,
                final String city,
                final String state,
                final UserType type,
                final double commission,
                final CommissionType commissionType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.active = active;
        this.admin = admin;
        this.email = email;
        this.contact = contact;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.type = type;
        this.commission = commission;
        this.commissionType = commissionType;
    }
    public User(final String firstName, final String lastName, final String userName, final String password,
                final String email,
                final String contact,
                final String address1,
                final String address2,
                final String city,
                final String state,
                final UserType type,
                final double commission,
                final CommissionType commissionType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.contact = contact;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.type = type;
        this.commission = commission;
        this.commissionType = commissionType;
    }
}
