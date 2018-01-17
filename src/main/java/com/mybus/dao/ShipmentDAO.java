package com.mybus.dao;

import com.mybus.model.Shipment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by srinikandula on 12/10/16.
 */
@Repository
public interface ShipmentDAO extends PagingAndSortingRepository<Shipment, String> {
    List<Shipment> findByFromBranchId(String fromBranchId);
    List<Shipment> findByToBranchId(String toBranchId);
}
