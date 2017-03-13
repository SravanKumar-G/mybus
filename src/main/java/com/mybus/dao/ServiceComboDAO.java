package com.mybus.dao;

import com.mybus.model.ServiceCombo;
import com.mybus.model.ServiceForm;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceComboDAO extends PagingAndSortingRepository<ServiceCombo, String> {
    Iterable<ServiceCombo> findByServiceNumber(String serviceNumber);
    Iterable<ServiceCombo> findByActive(boolean active);
}
