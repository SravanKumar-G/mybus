package com.mybus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author yks-srinivas
 *
 * This is DTO class.
 * We use this object for transfer data to client
 * example : generated hash code,paymentGateway information, merchantRefNo etc..
 */
public class Payment extends AbstractDocument {

	@Setter
	@Getter
	@ApiModelProperty(notes = "Id of the trip")
	private String tripId;
	@Setter
	@Getter
	private String seatId;

	@Setter
	@Getter
	private String email;

	@Setter
	@Getter
	private String phoneNo;

	@Setter
	@Getter
	private String firstName;

	@Setter
	@Getter
	private String lastName;

	@Setter
	@Getter
	private String paymentType;

	@Setter
	@Getter
	private String address;

	@Setter
	@Getter
	private String city;

	@Setter
	@Getter
	private String state;

	@Setter
	@Getter
	private String country;

	@Setter
	@Getter
	private String postalCode;

	@Setter
	@Getter
	private float amount;

	@Setter
	@Getter
	private String paymentId;

	@Setter
	@Getter
	private String merchantRefNo;
	
	@Setter
	@Getter
	private String paymentDate;
	
	@Setter
	@Getter
	private PaymentGateway paymentGateways;
	
	@Setter
	@Getter
	private String hashCode;

	@Setter
	@Getter
	private String currency;
	
	@Getter
	@Setter
	private String mode;
	
	@Getter
	@Setter
	private String algo;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private String channel;
	
}