/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author  schanda on 02/13/16.
 *
 */
public class ServiceBoardingPoint {

	@Getter
	@Setter
	private String boardingPointId;
	
	@Getter
	@Setter
	private Time pickupTime;
	
	@Getter
	@Setter
	private String landmark;
	
	@Getter
	@Setter
	private boolean haltAvailable;
	
}
