/**
 * 
 */
package com.mybus.model;

import org.joda.time.DateTime;

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
	private String pickupTime;

	@Getter
	@Setter
	private DateTime pickupTimeInDate;
	
}
