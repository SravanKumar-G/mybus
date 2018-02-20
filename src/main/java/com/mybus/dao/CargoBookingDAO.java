package com.mybus.dao;

import com.mybus.model.CargoBooking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by srinikandula on 12/10/16.
 */
@Repository
public interface CargoBookingDAO extends PagingAndSortingRepository<CargoBooking, String> {
    List<CargoBooking> findByFromBranchId(String fromBranchId);
    List<CargoBooking> findByToBranchId(String toBranchId);
}
