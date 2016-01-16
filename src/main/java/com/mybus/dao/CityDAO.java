package com.mybus.dao;

import com.mybus.model.City;
import com.mybus.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */
@Repository
public interface CityDAO extends PagingAndSortingRepository<City, String> {
    City findOneByName(String name);
    Iterable<City> findByName(String name);
    void delete(String s);

}
