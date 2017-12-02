package com.mybus.dao;

import com.mybus.model.FillingStation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmailNotificationEventDAO extends PagingAndSortingRepository<FillingStation, String> {

}
