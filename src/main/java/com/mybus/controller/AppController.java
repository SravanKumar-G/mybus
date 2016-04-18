package com.mybus.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mybus.service.PaymentManager;

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
	 * this is call back response from payu payment gateways.
	 *
	 */
	@RequestMapping(value = "/payUResponse", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payment(HttpServletRequest request,HttpServletResponse response) {
		LOGGER.info("response from payu paymentStatus");
		paymentManager.paymentResponseFromPayu(request);
		ModelAndView model = new ModelAndView();
		model.setViewName("../../index");
		return model;
	}
	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * this is call back response from EBS payment gateways.
	 *
	 */
	@RequestMapping(value = "/eBSResponse", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView paymentFromEbs(HttpServletRequest request,HttpServletResponse response) {
		LOGGER.info("response from ebs paymentStatus");
		paymentManager.paymentResponseFromEBS(request);
		ModelAndView model = new ModelAndView();
		model.setViewName("../../index");
		return model;
	}
}