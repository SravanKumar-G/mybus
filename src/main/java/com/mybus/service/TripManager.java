package com.mybus.service;

import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.TripDAO;
import com.mybus.model.BusService;
import com.mybus.model.Layout;
import com.mybus.model.Route;
import com.mybus.model.ServiceFrequency;
import com.mybus.model.Trip;

/**
 * 
 * @author yks-Srinivas
 *
 */
@Service
public class TripManager {

	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private LayoutDAO layoutDAO;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private BusServiceDAO busServiceDAO;

	public Iterable<Trip> getAllTrips() {
		return tripDAO.findAll();
	}

	public Trip createTrip(Trip trip) {
		return tripDAO.save(trip);
	}

	public void deleteAllTrips() {
		tripDAO.deleteAll();
	}

	public Trip getTripByID(String tripID) {
		return tripDAO.findOne(tripID);
	}

	/*
	 * The trips should be created for the service is being published. We may
	 * have to make this asynchronus call
	 */
	public void publishService(String serviceId) {
		BusService busService = busServiceDAO.findOne(serviceId);
		Preconditions.checkNotNull(serviceId, "Service Id can't be null");
		Set<DateTime> tripDates = getTripDates(busService);
		Layout layout = layoutDAO.findOne(busService.getLayoutId());
		Route route = routeDAO.findOne(busService.getRouteId());
		for (DateTime dateTime : tripDates) {
			Trip trip = new Trip();
			trip.setActive(true);
			trip.setAmenities(busService.getAmenities());
			// trip.setArrivalTime(busService.gets);
			// trip.setDepartureTime(departureTime);

			trip.setAvailableSeats(layout.getTotalSeats());
			trip.setBoardingPoints(busService.getBoardingPoints());
			trip.setDropingPoints(busService.getDropingPoints());
			trip.setFromCityId(route.getFromCity()); // this has to come from
														// via city
			trip.setLayoutId(layout.getId());
			trip.setRouteId(route.getId());
			trip.setRows(layout.getRows());
			// trip.setServiceFares(serviceFares);//This has to come from via
			// city
			trip.setServiceId(busService.getId());
			trip.setServiceName(busService.getServiceName());
			trip.setServiceNumber(busService.getServiceNumber());
			trip.setToCityId(route.getToCity());
			trip.setTotalSeats(layout.getTotalSeats());
			trip.setTripDate(dateTime);
			// trip.setVehicleAllotmentId(busService.get);
			createTrip(trip);
		}
	}

	/**
	 * 
	 * @param busService
	 * @return Collection of Date
	 */
	public Set<DateTime> getTripDates(BusService busService) {
		Set<DateTime> tripDates = new LinkedHashSet<>();
		DateTime nextDate = busService.getSchedule().getStartDate();

		ServiceFrequency frequency = busService.getSchedule().getFrequency();
		if (ServiceFrequency.DAILY.equals(frequency)) {
			while (!nextDate.isAfter(busService.getSchedule().getEndDate())) {
				tripDates.add(nextDate);
				nextDate = nextDate.plusDays(1);
			}
		} else if (ServiceFrequency.WEEKLY.equals(frequency)) {
			while (!nextDate.isAfter(busService.getSchedule().getEndDate())) {
				if (busService.getSchedule().getWeeklyDays().contains(DayOfWeek.of(nextDate.getDayOfWeek()))) {
					tripDates.add(nextDate);
				}
				nextDate = nextDate.plusDays(1);
			}
		} else if (ServiceFrequency.SPECIAL.equals(frequency)) {
			tripDates.addAll(busService.getSchedule().getSpecialServiceDates());
		}
		return tripDates;
	}
}
