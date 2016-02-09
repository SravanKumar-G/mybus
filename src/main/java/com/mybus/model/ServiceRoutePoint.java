package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author schanda on 02/04/16.
 *
 */
public class ServiceRoutePoint extends City {

	@Getter
	@Setter
	private boolean stopAvailable;
	
	@Getter
	@Setter
	private Time departureTime;
	
	@Getter
	@Setter
	private int journeyDay;
}
