package com.mybus.dao;

import com.mybus.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by skandula on 5/7/16.
 */
public interface BookingDAO extends PagingAndSortingRepository<Booking, String> {
}
