package com.mybus.model;

import java.time.DayOfWeek;
import java.util.Set;

import org.joda.time.DateTime;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author Suresh K
 *
 */
@ToString
@ApiModel(value = "Schedule")
public class Schedule {
	
	@Getter
	@Setter
	private DateTime startDate;
	
	@Getter
	@Setter
	private DateTime endDate;
	
	@Getter
	@Setter
	private ServiceFrequency frequency;
	
	@Setter
	@Getter
	private Set<DayOfWeek> weeklyDays;
	
	@Setter
	@Getter
	private Set<DateTime> specialServiceDates;
	
	public Schedule() {
		
	}
	
	public Schedule(DateTime startDate,DateTime endDate,ServiceFrequency frequency){
		this.startDate = startDate;
		this.endDate = endDate;
		this.frequency = frequency;
	}
	

}
