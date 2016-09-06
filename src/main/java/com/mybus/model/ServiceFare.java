/**
 * 
 */
package com.mybus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * @author  schanda on 02/13/16.
 *
 */
@NoArgsConstructor
public class ServiceFare {

	@Getter
	@Setter
	private String sourceCityId;

	@Getter
	@Setter
	private String destinationCityId;
	
	@Getter
	@Setter
	private double fare;
	
	@Getter
	@Setter
	private String arrivalTime;

	@Getter
	@Setter
	private String departureTime;
	

	@Getter
	@Setter
	private DateTime arrivalTimeInDate;

	@Getter
	@Setter
	private DateTime departureTimeInDate;

	@Getter
	@Setter
	@ApiModelProperty(notes = "The cut off time in minutes, copied from service configuration")
	private int cutOffTimeInMinutes;
	
	@Getter
	@Setter
	private double journeyDuration;
	
	@Getter
	@Setter
	private boolean active;

	public ServiceFare(String sourceCityId, String destinationCityId, boolean active) {
		this.sourceCityId = sourceCityId;
		this.destinationCityId = destinationCityId;
		this.active = active;
	}
}
