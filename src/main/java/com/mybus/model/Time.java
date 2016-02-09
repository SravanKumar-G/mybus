/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author schanda on 02/04/16.
 *
 */
public class Time {
	
	@Getter
	@Setter
	private int hour;
	
	@Getter
	@Setter
	private int minute;
	
	@Getter
	@Setter
	private TimeUnit meridian;
	

	private enum TimeUnit {

	    AM,
	    PM;

	    @Override
	    public String toString() {
	        return name();
	    }
	}
}
