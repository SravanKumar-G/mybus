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
public class Payment extends AbstractDocument{

	@Setter
	@Getter
	private String paymentType;

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