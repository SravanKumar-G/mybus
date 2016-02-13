package com.mybus.model;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
	private ServiceLayout layout;

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
	private ServiceRoute serviceRoute;

	@Getter
	@Setter
	private Set<String> boardingPoints;

	@Getter
	@Setter
	private Set<String> dropingPoints;

}
