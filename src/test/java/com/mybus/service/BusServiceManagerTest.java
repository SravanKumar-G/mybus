package com.mybus.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mybus.model.BusService;
import com.mybus.model.ServiceBoardingPoint;
import com.mybus.model.ServiceDropingPoint;
import com.mybus.model.ServiceFrequency;
import com.mybus.model.ServiceLayout;

public class BusServiceManagerTest {
	
	@Autowired
	private BusServiceManager busServiceManager;
	
	@Test
	public void saveBusService(){
		BusService busService = new BusService();
		busService.setActive(true);
		busService.setCutoffTime("2015-12-31T18:30:00.000Z");
		busService.setEffectiveFrom("2015-12-31T18:30:00.000Z");
		busService.setServiceName("testName");
		busService.setServiceNumber("1234");
		busService.setPhoneEnquiry("9492541342");
		busService.setCutoffTime("3");
		busService.setServiceTax(new BigDecimal("10"));
		busService.setServiceTaxType("PERCENTAGE");
		
		ServiceFrequency sf = new ServiceFrequency();
		sf.setServiceType("DAILY");
		busService.setFrequency(sf);
		
		Set<ServiceBoardingPoint> sbpSet = new HashSet<ServiceBoardingPoint>(); 
		ServiceBoardingPoint sbp = new ServiceBoardingPoint();
		sbp.setBoardingPointId("571a23ee84ae0784faf60197");
		sbp.setPickupTime("1969-12-31T19:30:00.000Z");
		sbpSet.add(sbp);
		busService.setBoardingPoints(sbpSet);

		Set<ServiceDropingPoint> sdpSet = new HashSet<ServiceDropingPoint>(); 
		ServiceDropingPoint sdp = new ServiceDropingPoint();
		sdp.setDroppingPointId("571a23ee84ae0784faf60197");
		sdp.setDroppingTime("1969-12-31T19:30:00.000Z");
		sdpSet.add(sdp);
		busService.setDropingPoints(sdpSet);
		
		ServiceLayout layout = new ServiceLayout();
		layout.setLayoutId("571a255184ae0784faf601cc");
		//busService.setLayout(layout);
		busServiceManager.saveBusService(busService);
	}
}
