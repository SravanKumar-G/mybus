package com.mybus.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Payment;
import com.mybus.model.User;
import com.mybus.service.PaymentManager;

import com.mybus.model.PaymentResponse;
import com.mybus.model.RefundResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import com.mybus.util.Status;


/**
 * 
 * @author yks-srinivas
 * 
 * this is common controller for payment gateways
 * Use this controller to get payment gateway details.
 *
 */

@Controller
@RequestMapping(value = "/api/v1")
public class PaymentController {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
	
	
	@Autowired
	public PaymentManager paymentManager;

	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "payment/payu", method = RequestMethod.POST, 
							produces = ControllerUtils.JSON_UTF8,
							consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Payment request")
	public Payment getPayuHasCode(HttpServletRequest request,@RequestBody final Payment payment) {
		LOGGER.info("Got request to payment process");
		return paymentManager.getPayuPaymentGatewayDetails(payment);
	}
	
	
	/**
     * 
     * @param request
     * @return
     * this is call back response from payment gateways. 
     * 
     */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "payment/payuResponse", method = {RequestMethod.GET,RequestMethod.POST},produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Payment request")
	public ModelAndView paymentResponse(HttpServletRequest request) {
		LOGGER.info("Got request to payment process");
		PaymentResponse paymentResponse = paymentManager.paymentResponseFromPayu(request);
        ModelAndView model = new ModelAndView();
        model.setViewName("index");
        return model;

	}
	

	@ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "payments", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the payments details", response = PaymentResponse.class, responseContainer = "List")
    public Iterable<PaymentResponse> getPaymentDetails(HttpServletRequest request) {
        return paymentManager.getPaymentDetails();
    }

	
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "paymentRefund", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Amount Refund process to payment", response = String.class)
    public RefundResponse refundProcessToPaymentGateways(HttpServletRequest request,@RequestBody final String paymentid) {
    	return paymentManager.refundProcessToPaymentGateways(paymentid);
    }

}