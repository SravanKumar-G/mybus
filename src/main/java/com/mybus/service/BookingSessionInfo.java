package com.mybus.service;

import java.util.ArrayList;
import java.util.List;

import com.mybus.model.BusJourney;
import com.mybus.model.JourneyType;

import lombok.Getter;
import lombok.Setter;

public class BookingSessionInfo {
	
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
	private List<BusJourney> busJournies = new ArrayList<>();

	public void setBusJourney(String fromCity,String ToCity ,String dateOfJourney,String returnJourney, JourneyType journeyType){
		if(JourneyType.ONE_WAY.equals(journeyType)){
			busJournies.add(new BusJourney(fromCity,ToCity,dateOfJourney,journeyType));
		}else{
			busJournies.add(new BusJourney(fromCity,ToCity,dateOfJourney,JourneyType.ONE_WAY));
			busJournies.add(new BusJourney(ToCity,fromCity,returnJourney,JourneyType.TWO_WAY));
		}
	}
}
