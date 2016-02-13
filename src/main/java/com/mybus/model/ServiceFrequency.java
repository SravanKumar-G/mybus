package com.mybus.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

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
