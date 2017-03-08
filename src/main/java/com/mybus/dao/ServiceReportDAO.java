package com.mybus.dao;

import com.mybus.model.ServiceReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceReportDAO extends PagingAndSortingRepository<ServiceReport, String> {
    Iterable<ServiceReport> findByJourneyDate(String date);
}
