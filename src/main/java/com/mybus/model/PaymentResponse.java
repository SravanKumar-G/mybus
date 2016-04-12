package com.mybus.model;

import java.util.Date;
import java.util.Map;

import com.mybus.util.Status;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

/**
 * 
 * @author yks-Srinivas
 * payment response object
 */
@ToString
@ApiModel(value = "PaymentResponse")
public class PaymentResponse extends AbstractDocument {
	
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

	@Setter
	@Getter
	private JSONObject responseParams;
}
