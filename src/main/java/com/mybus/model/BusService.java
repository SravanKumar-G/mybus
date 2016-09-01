package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
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
	@ApiModelProperty(notes = "Cut off time in minutes for disabling reservations " +
			"for this service before the bus starts")
	private int cutoffTime;
	
	@Getter
	@Setter
	private ServiceTaxType serviceTaxType;

	@Getter
	@Setter
	private double serviceTax;

	@Getter
	@Setter
	private String layoutId;

	@Getter
	@Setter
	private String routeId;

	@Getter
	@Setter
	private String routeName;
	
	@Getter
	@Setter
	private Schedule schedule;

	@Getter
	@Setter
	private Set<ServiceAmenity> amenities = new LinkedHashSet<ServiceAmenity>();

	@Getter
	@Setter
	private Set<ServiceBoardingPoint> boardingPoints = new LinkedHashSet<ServiceBoardingPoint>();

	@Getter
	@Setter
	private Set<ServiceDropingPoint> dropingPoints = new LinkedHashSet<ServiceDropingPoint>();

	@Getter
	@Setter
	@ApiModelProperty(notes = "Fares for combination of routes with different cities")
	private List<ServiceFare> serviceFares;
	
	@Getter
	@Setter
	private String status;
	
	@Getter
	@Setter
	private double fare;

	public void addBoardingPoints(List<BoardingPoint> list) {
		list.stream().filter(bp -> bp.isActive()).forEach(bp -> {
			this.getBoardingPoints().add(new ServiceBoardingPoint(bp));
		});
	}
	public void addDroppingPoints(List<BoardingPoint> list) {
		list.stream().filter(bp -> bp.isDroppingPoint() && bp.isActive()).
				forEach(dp -> this.getDropingPoints().add(new ServiceDropingPoint(dp)));
	}
	
}
