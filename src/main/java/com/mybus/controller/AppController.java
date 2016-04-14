package com.mybus.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mybus.dao.PaymentResponseDAO;
import com.mybus.model.PaymentResponse;
import com.mybus.service.PaymentManager;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

	@Autowired
	PaymentManager paymentManager;
	
	@RequestMapping(value = { "/", "/helloworld**" }, method = RequestMethod.GET)
	public ModelAndView welcomePage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security 3.2.3 Hello World Application");
		model.addObject("message", "Welcome Page !");
		model.setViewName("helloworld");
		return model;

	}

	@RequestMapping(value = "/protected**", method = RequestMethod.GET)
	public ModelAndView protectedPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security 3.2.3 Hello World");
		model.addObject("message", "This is protected page - Only for Administrators !");
		model.setViewName("protected");
		return model;

	}

	@RequestMapping(value = "/confidential**", method = RequestMethod.GET)
	public ModelAndView superAdminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security 3.2.3 Hello World");
		model.addObject("message", "This is confidential page - Need Super Admin Role !");
		model.setViewName("protected");

		return model;

	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login");

        return model;

    }
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        model.setViewName("index");
        return model;
    }

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * this is call back response from payment gateways.
	 *
	 */
	@RequestMapping(value = "/payUResponse", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payment(HttpServletRequest request,HttpServletResponse response) {
		LOGGER.info("response from payu paymentStatus");
		/*Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> mapData = request.getParameterMap();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setAmount(Double.parseDouble(map.get("amount")));
		paymentResponse.setPaymentId(map.get("txnid"));
		paymentResponse.setMerchantrefNo(map.get("mihpayid"));
		paymentResponse.setPaymentDate(map.get("addedon"));
		paymentResponse.setPaymentType(map.get(""));
		paymentResponse.setPaymentName(map.get(""));
		paymentResponse.setResponseParams(new JSONObject(mapData));*/
		paymentManager.paymentResponseFromPayu(request);
		//paymentResponseDAO.save(paymentResponse);
		//LOGGER.info("got response from payu pg"+map);
		ModelAndView model = new ModelAndView();
		model.setViewName("../../index");
		return model;
	}


}
