package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(value = "Agent")
@Getter
@Setter
@EqualsAndHashCode(of={"id"})
public class Agent extends AbstractDocument {
    private String name;
    private boolean active;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String landline;
    private String address;
    private String branchOfficeId;
    private String branchName;
}
