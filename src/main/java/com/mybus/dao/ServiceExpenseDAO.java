package com.mybus.dao;

import com.mybus.model.ServiceExpense;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ServiceExpenseDAO extends PagingAndSortingRepository<ServiceExpense, String> {
    Iterable<ServiceExpense> findByJourneyDate(Date date);
    ServiceExpense findByJourneyDateAndServiceNumber(Date date, String serviceNUmber);

}
