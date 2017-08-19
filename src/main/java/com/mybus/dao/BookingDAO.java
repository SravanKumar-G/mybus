package com.mybus.dao;

import com.mybus.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by skandula on 5/7/16.
 */
@Repository
public interface BookingDAO extends PagingAndSortingRepository<Booking, String> {
    Iterable<Booking> findByServiceId(String serviceId);
    Iterable<Booking> findByIdAndHasValidAgent(String id, boolean hasValidAgent);
    Iterable<Booking> findByFormId(String formId);
    Iterable<Booking> findByDue(boolean due);
    List<Booking> findByPhoneNo(String phoneNumber);
    Iterable<Booking> findByBookedByAndDue(String bookedBy, boolean due);
    Iterable<Booking> findByBookedByAndHasValidAgent(String bookedBy, boolean hasValidAgent);
    Booking findByTicketNo(String ticketNo);
    void deleteByServiceId(String serviceId);
    void deleteByFormId(String formId);

}
