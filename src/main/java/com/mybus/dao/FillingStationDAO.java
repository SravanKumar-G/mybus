package com.mybus.dao;

import com.mybus.model.Amenity;
import com.mybus.model.FillingStation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Repository
public interface FillingStationDAO extends PagingAndSortingRepository<FillingStation, String> {
}
