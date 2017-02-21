package com.mybus.dao;

import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceReportStatusDAO extends PagingAndSortingRepository<ServiceReportStatus, String> {
    Iterable<ServiceReportStatus> findByReportDate(String downloadedOn);

}
