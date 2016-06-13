package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Booking;
import com.mybus.model.Payment;
import com.mybus.model.PaymentResponse;
import com.mybus.model.RefundResponse;
import com.mybus.service.PaymentManager;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author yks-srinivas
 * 
 * this is common controller for payment gateways
 * Use this controller to get payment gateway details.
 *
 */

@RestController
@RequestMapping(value = "/api/v1")
public class PaymentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	public PaymentManager paymentManager;
	
	@RequiresAuthorizedUser(value=false)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "payment", method = RequestMethod.POST, 
							produces = ControllerUtils.JSON_UTF8,
							consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Initiate booking")
	public Payment initiateBooking(HttpServletRequest request,@RequestBody final Payment payment) {
		LOGGER.info("Got request to payment process");
		if(payment.getPaymentType().equalsIgnoreCase("EBS")){
			return paymentManager.getEBSPaymentGatewayDetails(payment);
		}else {
			return paymentManager.getPayuPaymentGatewayDetails(payment);
		}
	}
	
	@ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "payments", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the payments details", response = PaymentResponse.class, responseContainer = "List")
    public Iterable<PaymentResponse> getPaymentDetails(HttpServletRequest request) {
        return paymentManager.getPaymentDetails();
    }
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "paymentRefund", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Refund Payment request")
	public RefundResponse refundProcessToPaymentGateways(HttpServletRequest request,
			@RequestParam("pID") String pID,
			@RequestParam("refundAmount") double refundAmount,
			@RequestParam("disc") String disc) {
		return paymentManager.refundProcessToPaymentGateways(pID,refundAmount,disc);
    }
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getRefundAmount", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Refund Payment request")
	public double getRefundAmount(HttpServletRequest request,
			@RequestParam("pID") String pID,
			@RequestParam("seatFare") double seatFare) {
		
		DateTime busStartTime = new DateTime();
		return paymentManager.refundAmount(busStartTime,seatFare);
    }
	
}