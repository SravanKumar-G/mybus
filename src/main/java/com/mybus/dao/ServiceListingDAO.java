package com.mybus.dao;

import com.mybus.model.ServiceListing;
import com.mybus.model.ServiceReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ServiceListingDAO extends PagingAndSortingRepository<ServiceListing, String> {
    Iterable<ServiceListing> findByJourneyDate(Date date);

    ServiceListing findByJourneyDateAndServiceNumber(Date date, String serviceNUmber);
}
