package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mybus.model.Amenity;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Repository
public interface AmenityDAO  extends PagingAndSortingRepository<Amenity, String> {

}
