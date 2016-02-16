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
public class ServiceDropingPoint {

	@Getter
	@Setter
	private String droppingPointId;

	@Getter
	@Setter
	private Time droppingTime;
	
}
