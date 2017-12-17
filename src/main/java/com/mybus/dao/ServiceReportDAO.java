package com.mybus.dao;

import com.mybus.model.ServiceReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ServiceReportDAO extends PagingAndSortingRepository<ServiceReport, String> {
    Iterable<ServiceReport> findByJourneyDate(Date date);
    ServiceReport findByJourneyDateAndServiceNumber(Date date, String serviceNUmber);
    ServiceReport findByJourneyDateAndServiceId(Date date, String serviceId);

}
