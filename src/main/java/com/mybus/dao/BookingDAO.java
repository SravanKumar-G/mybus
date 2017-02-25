package com.mybus.dao;

import com.mybus.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 5/7/16.
 */
@Repository
public interface BookingDAO extends PagingAndSortingRepository<Booking, String> {
    Iterable<Booking> findByServiceId(String serviceId);
    Iterable<Booking> findByFormId(String formId);
}
