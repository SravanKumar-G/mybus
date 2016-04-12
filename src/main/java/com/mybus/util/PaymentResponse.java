package com.mybus.util;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author yks-Srinivas
 * payment response object
 */
public class PaymentResponse {
	
	@Setter
	@Getter
	private double amount;
	
	@Setter
	@Getter
	private String paymentId;
	
	@Setter
	@Getter
	private String merchantrefNo;
	
	@Setter
	@Getter
	private Date paymentDate;
	
	/**
	 * Type of payment method, paymentgateway (PG) or Wallet
	 */
	@Setter
	@Getter
	private String paymentType;
	
	/**
	 * Name of the paymentType Ex: Payu, Paytm,EBS,Mobikwik
	 */
	@Setter
	@Getter
	private String paymentName;
	
	@Setter
	@Getter
	private Status status;
}
