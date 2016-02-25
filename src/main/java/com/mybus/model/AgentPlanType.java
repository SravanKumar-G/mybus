package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by svanik on 2/22/2016.
 */

@ToString
@NoArgsConstructor
public class AgentPlanType extends AbstractDocument {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String commissionType;

    @Getter
    @Setter
    private Double deposit;

    @Getter
    @Setter
    private Double balance;

    @Getter
    @Setter
    private String settlementFrequency;

    @Getter
    @Setter
    private Double threshold;
}
