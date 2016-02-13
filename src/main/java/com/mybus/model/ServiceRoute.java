package com.mybus.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author schanda on 02/04/16.
 *
 */
public class ServiceRoute {

	@Getter
	@Setter
	private String routeId;
	
	@Getter
	@Setter
	private String routeName;
	
	@Getter
	@Setter
	private City originCity;
	
	@Getter
	@Setter
	private City destinationCity;
	
	@Getter
	@Setter
	private List<ServiceRoutePoint> routePoints;
	
}
