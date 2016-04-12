package com.mybus.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mybus.util.Status;
import com.mybus.model.Payment;
import com.mybus.model.PaymentGateways;
import com.mybus.util.Constants;
import com.mybus.model.PaymentResponse;

/**
 * 
 * @author yks-Srinivas
 */
@Service
public class PaymentManager {


	private static final Logger LOGGER= LoggerFactory.getLogger(PaymentManager.class);

	public Payment getPayuPaymentGatewayDetails(Payment payment){

		LOGGER.info("PaymentManager :: getPayuPaymentGatewayDetails");
		PaymentGateways pg = new PaymentGateways();
		pg.setPgKey("eCwWELxi"); //payu  salt
		pg.setPgAccountID("gtKFFx"); //payu key
		pg.setPgRequestUrl("https://test.payu.in/_payment");
		pg.setPgCallbackUrl("http://localhost:8081/payUResponse");
		String merchantRefNo =  getRandamNo();
		String hashSequence = pg.getPgAccountID()+"|"+ merchantRefNo +"|"+ (int)payment.getAmount() +"|bus|"+ payment.getFirstName() +"|"+ payment.getEmailID() +"|||||||||||"+pg.getPgKey();
		LOGGER.info("hashSequence - "+hashSequence);		
		String hash = hashCal(Constants.SHA_512,hashSequence);
		LOGGER.info("secured hash - "+hash);
		payment.setHashCode(hash);
		payment.setPaymentGateways(pg);
		payment.setMerchantRefNo(merchantRefNo);
		return payment;
	}

	public PaymentResponse paymentResponseFromPayu(HttpServletRequest request) {

		PaymentResponse paymentResponse = new PaymentResponse();
		Status status = new Status();
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> mapData = request.getParameterMap();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			map.put(paramName, mapData.get(paramName)[0]);
		}
		LOGGER.info("got response from payu pg"+map);

		String hashSequence = "eCwWELxi|"+map.get("status")+"|||||||||||"+ map.get("email") +"|"+ map.get("firstname") +"|"+ map.get("productinfo")+"|"+ map.get("amount") +"|"+ map.get("txnid") +"|"+map.get("key");

		if(!hashCal(Constants.SHA_512,hashSequence).equalsIgnoreCase(map.get("hash")) || !Constants.status.SUCCESS.name().equalsIgnoreCase(map.get("status")) || !Constants.PAYU_SUCCESS_CODE.equalsIgnoreCase(map.get("error"))){
			status.setSuccess(false);
			status.setStatusCode(Constants.FAILED_CODE);
			status.setStatusMessage("Payment has failed");
			paymentResponse.setStatus(status);
			LOGGER.info("hash failed from payu.. request and response hash both are not same...");	
		}else{
			status.setSuccess(true);
			status.setStatusCode(Constants.SUCCESS_CODE);
			status.setStatusMessage("Payment has success");
			paymentResponse.setAmount(Double.parseDouble(map.get("amount")));
			paymentResponse.setPaymentId(map.get("payuMoneyId"));
			paymentResponse.setMerchantrefNo(map.get("txnid"));
			paymentResponse.setPaymentDate(new Date());
			paymentResponse.setStatus(status);

		}
		return paymentResponse;
	}

	private String getRandamNo(){
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}

	private String hashCal(String type,String str){
		byte[] hashseq=str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try{
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();
			for (int i=0;i<messageDigest.length;i++) {
				String hex=Integer.toHexString(0xFF & messageDigest[i]);
				if(hex.length()==1){ 
					hexString.append("0");
				}
				hexString.append(hex);
			}
		}catch(NoSuchAlgorithmException nsae){ 
		}
		return hexString.toString();
	}	
}