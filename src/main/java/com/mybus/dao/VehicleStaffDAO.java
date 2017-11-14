package com.mybus.dao;

import com.mybus.model.VehicleStaff;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface VehicleStaffDAO extends PagingAndSortingRepository<VehicleStaff, String> {
    Iterable<VehicleStaff> findByDlExpiryLessThan(Date date);
}
