package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "BusService")
public class BusService extends AbstractDocument {

	@Getter
	@Setter
	private boolean active;

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
	private Time cutoffTime;
	
	@Getter
	@Setter
	private ServiceTaxType serviceTaxType;

	@Getter
	@Setter
	private BigDecimal serviceTax; 

	@Getter
	@Setter
	private String layoutId;

	@Getter
	@Setter
	private Date effectiveFrom;
	
	@Getter
	@Setter
	private Date effectiveTo;

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
