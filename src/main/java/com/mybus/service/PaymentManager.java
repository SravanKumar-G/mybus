package com.mybus.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.mybus.util.Status;
import com.google.common.base.Preconditions;
import com.mybus.dao.PaymentResponseDAO;
import com.mybus.model.Payment;
import com.mybus.model.PaymentGateways;
import com.mybus.util.Constants;
import com.mybus.model.PaymentResponse;
import com.mybus.model.RefundResponse;
import com.mybus.model.Route;


/**
 * 
 * @author yks-Srinivas
 */
@Service
public class PaymentManager {


	private static final Logger LOGGER= LoggerFactory.getLogger(PaymentManager.class);
	
	@Autowired
	PaymentResponseDAO paymentResponseDAO;

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
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> mapData = request.getParameterMap();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			map.put(paramName, mapData.get(paramName)[0]);
		}
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setAmount(Double.parseDouble(map.get("amount")));
		paymentResponse.setPaymentId(map.get("txnid"));
		paymentResponse.setMerchantrefNo(map.get("mihpayid"));
		paymentResponse.setPaymentDate(map.get("addedon"));
		paymentResponse.setPaymentType(map.get(""));
		paymentResponse.setPaymentName(map.get(""));
		paymentResponse.setResponseParams(new JSONObject(mapData));
		Status status = new Status();
		String hashSequence = "eCwWELxi|"+map.get("status")+"|||||||||||"+ map.get("email") +"|"+ map.get("firstname") +"|"+ map.get("productinfo")+"|"+ map.get("amount") +"|"+ map.get("txnid") +"|"+map.get("key");
		if(!hashCal(Constants.SHA_512,hashSequence).equalsIgnoreCase(map.get("hash")) || !Constants.status.SUCCESS.name().equalsIgnoreCase(map.get("status")) || !Constants.PAYU_SUCCESS_CODE.equalsIgnoreCase(map.get("error"))){
			status.setSuccess(false);
			status.setStatusCode(Constants.FAILED_CODE);
			status.setStatusMessage("Payment has failed");
			LOGGER.info("hash failed from payu.. request and response hash both are not same...");	
		}else{
			status.setSuccess(true);
			status.setStatusCode(Constants.SUCCESS_CODE);
			status.setStatusMessage("Payment has success");
		}
		paymentResponse.setStatus(status);
		paymentResponseDAO.save(paymentResponse);
		return paymentResponse;
	}

	/**
	 * @param paymentResponse
	 * @return
	 * This is the common method for refund amount to all payment gateways 
	 */
	public RefundResponse refundProcessToPaymentGateways(String paymentid) {
		PaymentResponse paymentResponse = paymentResponseDAO.findOne(paymentid);
		String uniqueId = getRandamNo();
		String hashSequence =null;
		hashSequence = "gtKFFx|cancel_refund_transaction|"+paymentResponse.getMerchantrefNo()+"|eCwWELxi";
		java.net.URL url;
		java.io.OutputStreamWriter wr = null;
		RefundResponse refundResponse = new RefundResponse();
		try {
			url = new java.net.URL("https://test.payu.in/merchant/postservice?form=2");
			java.net.URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			wr = new java.io.OutputStreamWriter(conn.getOutputStream());
			wr.write("key=gtKFFx&command=cancel_refund_transaction&hash="+ hashCal("SHA-512",hashSequence) +"&var1="+ paymentResponse.getMerchantrefNo() +"&var2="+ uniqueId +"&var3="+paymentResponse.getAmount());
			LOGGER.info("In util:refundAmountToPaymentGateWays PAYU call Refund request ::"+hashSequence);
			wr.flush();
			java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuilder rowResonse = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				rowResonse.append(line);
				LOGGER.info("In util:refundAmountToPaymentGateWays PAYU call Refund response ::"+line);
			}
			Status status = new Status();
			status.setSuccess(true);
			status.setStatusCode(Constants.SUCCESS_CODE);
			status.setStatusMessage("Refund has success");
			refundResponse.setStatus(status);
			refundResponse.setRefundResponseParams(rowResonse.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(wr!=null){
				try {
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		paymentResponse.setRefundResponse(refundResponse);
		update(paymentResponse);
		return refundResponse;
	}
	
	public Iterable<PaymentResponse> getPaymentDetails() {
        return paymentResponseDAO.findAll();
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
	

	public boolean update(PaymentResponse paymentResponse) {
        
        PaymentResponse pr = paymentResponseDAO.findOne(paymentResponse.getId());
        try {
        	pr.merge(paymentResponse);
        	paymentResponseDAO.save(pr);
        } catch (Exception e) {
        	LOGGER.error("Error updating the Route ", e);
           throw new RuntimeException(e);
        }
        return true;
    }

}