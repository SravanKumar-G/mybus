package com.mybus.dao;

import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

@Repository
public interface ServiceReportDAO extends PagingAndSortingRepository<ServiceReport, String> {
    Iterable<ServiceReport> findByJourneyDate(String date);
}
