package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Created by schanda on 02/02/16.
 */
public class ServiceFrequency {

	@Getter
	@Setter
	private ServiceType serviceType;

	@Getter
	@Setter
	private List<String> weeklyDays;

	@Getter
	@Setter
	private List<Date> specialServiceDates;
}
