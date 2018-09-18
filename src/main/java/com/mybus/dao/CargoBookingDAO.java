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
    CargoBooking findByIdAndOperatorId(String id, String operatorId);
    List<CargoBooking> findByFromBranchId(String fromBranchId, String operatorId);
    List<CargoBooking> findByToBranchId(String toBranchId, String operatorId);
    CargoBooking findOneByFromContactAndOperatorId(long contact, String operatorId);
    CargoBooking findOneByToContactAndOperatorId(long contact, String operatorId);
    CargoBooking findByShipmentNumberAndOperatorId(String lrNumber, String operatorId);
}
