package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "BusService")
public class BusService extends AbstractDocument {

	@Getter
	@Setter
	private String active;

	@Getter
	@Setter
	private String serviceName;

	@Getter
	@Setter
	private String serviceNumber;

	@Getter
	@Setter
	private String phoneEnquiry;

	@Getter
	@Setter
	private String cutoffTime;
	
	@Getter
	@Setter
	private DateTime cutoffTimeInDate;
	
	
	@Getter
	@Setter
	//private ServiceTaxType serviceTaxType;
	private String serviceTaxType;

	@Getter
	@Setter
	private BigDecimal serviceTax; 

	@Getter
	@Setter
	private ServiceLayout layout;

	@Getter
	@Setter
	private String layoutId;

	@Getter
	@Setter
	private String routeId;

	@Getter
	@Setter
	//yyyy-MM-dd
	private DateTime effectiveFrom;

	@Getter
	@Setter
	private DateTime effectiveTo;

	@Getter
	@Setter
	private ServiceFrequency frequency;
	
	@Getter
	@Setter
	private Set<ServiceBoardingPoint> boardingPoints;

	@Getter
	@Setter
	private Set<ServiceDropingPoint> dropingPoints;
	
	@Getter
	@Setter
	private List<ServiceFare> serviceFares;

}
