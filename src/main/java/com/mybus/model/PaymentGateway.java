package com.mybus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
public class PaymentGateway extends AbstractDocument {
    @Getter
    @Setter
    private String apiKey;

    @Getter
    @Setter
    private String accountId;

    @Getter
    @Setter
    private String getwayUrl;

    @Getter
    @Setter
    @ApiModelProperty(notes = "PayU, EBS etc")
    private String gatewayType;

    @Getter
    @Setter
    @ApiModelProperty(notes = "Gateway, Wallet etc")
    private String paymentType;

    @Getter
    @Setter
    @Indexed(unique=true)
    private String name;
}
