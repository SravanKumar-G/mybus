package com.mybus.service;

import java.util.List;

import com.mybus.model.BusJourney;
import com.mybus.model.JourneyType;

import lombok.Getter;
import lombok.Setter;

public class bookingSessionInfo {
	
	/**
	 * We use this variable to find type of journey like ONE_WAY/TWO_WAY journey
	 */
	@Setter
	@Getter
	private JourneyType journeyType;
	
	@Setter
	@Getter
	private String customerPhoneNumber;
	
	@Setter
	@Getter
	private String customerEMail;
	
	@Setter
	@Getter
	private List<BusJourney> busJournies;
 
}
