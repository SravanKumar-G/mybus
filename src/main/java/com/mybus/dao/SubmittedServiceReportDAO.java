package com.mybus.dao;

import com.mybus.model.SubmittedServiceReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmittedServiceReportDAO extends PagingAndSortingRepository<SubmittedServiceReport, String> {
    Iterable<SubmittedServiceReport> findByJDate(String date);
}
