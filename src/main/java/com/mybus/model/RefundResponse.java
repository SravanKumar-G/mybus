package com.mybus.model;

import java.util.Date;

import com.mybus.util.Status;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author yks-Srinivas
 *
 */
public class RefundResponse {
	
	@Setter
	@Getter
	private double refundAmout;
	
	@Setter
	@Getter
	private Date refundDate;
	
	@Setter
	@Getter
	private String paymentId;
	
	@Setter
	@Getter
	private String refundId;
	
	@Setter
	@Getter
	private String merchentrefNo;
	
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
