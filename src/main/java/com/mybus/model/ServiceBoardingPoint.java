/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * @author  schanda on 02/13/16.
 *
 */
@NoArgsConstructor
public class ServiceBoardingPoint {

	@Getter
	@Setter
	private String refId;
	
	@Getter
	@Setter
	private String time;
	
	@Getter
	@Setter
	private String bpName;

	@Getter
	@Setter
	private DateTime timeInDate;

	public ServiceBoardingPoint(BoardingPoint bp){
		this.refId = bp.getId();
	}
}
