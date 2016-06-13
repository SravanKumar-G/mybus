package com.mybus.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
/**
 * This busjourn
 */
public class BusJourney {
	
	@Setter
	@Getter
	private String fromCity;
	
	@Setter
	@Getter
	private String toCity;
	
	@Setter
	@Getter
	private String dateOfJourney;
	
	/**
	 * Busjourney is one way set it as ONE_WAY 
	 * Busjourney is round trip set it TWO_WAY
	 */
	@Setter
	@Getter
	private JourneyType journeyType;
	
	@Setter
	@Getter
	private double fare;
	
	@Setter
	@Getter
	private double totalFare;
	
	@Setter
	@Getter
	private String serviceNumber;
	
	@Setter
	@Getter
	private Set<String> seatsNumber;
	
	@Setter
	@Getter
	private String serviceName;
	

    @Getter
    @Setter
    private Set<ServiceBoardingPoint> boardingPoints;

    @Getter
    @Setter
    private Set<ServiceDropingPoint> dropingPoints;
	
	public BusJourney(String fromCity,String toCity, String dateOfJourney,JourneyType journeyType){
		this.fromCity=fromCity;
		this.toCity=toCity;
		this.dateOfJourney=dateOfJourney;
		this.journeyType=journeyType;
	}
}
