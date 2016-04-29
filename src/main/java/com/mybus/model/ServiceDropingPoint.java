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
public class ServiceDropingPoint {

	@Getter
	@Setter
	private String droppingPointId;

	@Getter
	@Setter
	private String droppingTime;

	@Getter
	@Setter
	private DateTime droppingTimeInDate;
	
}
