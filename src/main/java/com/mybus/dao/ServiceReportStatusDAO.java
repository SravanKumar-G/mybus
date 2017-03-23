package com.mybus.dao;

import com.mybus.model.ServiceReportStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ServiceReportStatusDAO extends PagingAndSortingRepository<ServiceReportStatus, String> {
    Iterable<ServiceReportStatus> findByReportDate(Date downloadedOn);
    void deleteByReportDate(Date date);

}
