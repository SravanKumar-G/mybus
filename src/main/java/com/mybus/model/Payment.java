package com.mybus.model;

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
public class Payment {
	
	@Setter
	@Getter
	private String emailID;
	
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
	private String country;
	
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
	private PaymentGateways paymentGateways;
	
	@Setter
	@Getter
	private String hashCode;
}