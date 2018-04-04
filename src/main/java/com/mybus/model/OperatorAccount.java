package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OperatorAccount extends AbstractDocument {

    public static String ABHIBUS = "Abhibus";
    public static String Bitlabus = "Bitlabus";


    @RequiresValue
    @Indexed(unique = true)
    private String name;

    @RequiresValue
    @Indexed(unique = true)
    private String domainName;

    @RequiresValue
    @Indexed(unique = true)
    private String apiURL;

    @RequiresValue
    private String providerType; // Abhibus, Bitla Etc

    @RequiresValue
    private String userName;

    @RequiresValue
    private String password;

    private boolean active;

    private boolean skipAgentValidity;

    private String onlineBookingTypes;

}
