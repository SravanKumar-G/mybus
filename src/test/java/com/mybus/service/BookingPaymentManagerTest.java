package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.PaymentResponseDAO;
import com.mybus.model.PaymentResponse;
import com.mybus.util.Status;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BookingPaymentManagerTest extends AbstractControllerIntegrationTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookingPaymentManagerTest.class);
	
	@Autowired
	private PaymentResponseDAO paymentResponseDAO;
	
	@Autowired
	private BookingPaymentManager paymentManager;

	@Autowired
	private SystemProperties systemProperties;

	@Test
	public void addAmount(){
		PaymentResponse pr = new PaymentResponse();
		pr.setAmount(100);
		pr.setMerchantrefNo("403993715514355049");
		pr.setPaymentId("1461056823205");
		pr.setPaymentType("PG");
		pr.setPaymentName("PAYU");
		Status status = new Status();
		status.setStatusCode(200);
		status.setStatusMessage("BookingPayment has success");
		status.setSuccess(true);
		DateTime doj = new DateTime(2016,04,25,11,30);
		pr.setStatus(status);
		paymentResponseDAO.save(pr);
	}
	
	@Test
	public void refundAmount(){
		DateTime busStartTime = new DateTime(2016,05,25,11,30);
		paymentManager.refundAmount(busStartTime,100);
	}

}
