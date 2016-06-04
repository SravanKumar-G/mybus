/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

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
	public ServiceDropingPoint(BoardingPoint bp) {
		this.droppingPointId = bp.getId();
	}
	
}
