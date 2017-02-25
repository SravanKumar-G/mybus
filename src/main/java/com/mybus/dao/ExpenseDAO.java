package com.mybus.dao;

import com.mybus.model.Expense;
import com.mybus.model.ExpenseType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */
@Repository
public interface ExpenseDAO extends PagingAndSortingRepository<Expense, String> {
    Iterable<Expense> findByType(ExpenseType type);
    Iterable<Expense> findByServiceId(String serviceId);
}
