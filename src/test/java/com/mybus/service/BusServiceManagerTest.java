package com.mybus.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.mybus.controller.AbstractControllerIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mybus.model.BusService;
import com.mybus.model.ServiceBoardingPoint;
import com.mybus.model.ServiceDropingPoint;
import com.mybus.model.ServiceFrequency;
import com.mybus.model.ServiceLayout;

public class BusServiceManagerTest extends AbstractControllerIntegrationTest {
	
	@Autowired
	private BusServiceManager busServiceManager;

}
