package com.mybus.model;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;
/**
 * This busjourn
 */
public class BusJourney {
	
	@Setter
	@Getter
	private String source;
	
	@Setter
	@Getter
	private String destination;
	
	@Setter
	@Getter
	private DateTime dateOfJourney;
	
	/**
	 * Busjourney is one way set it as ONE_WAY 
	 * Busjourney is round trip set it TWO_WAY
	 */
	@Setter
	@Getter
	private JourneyType journeyType;
	
	
}
