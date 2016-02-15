package com.mybus.dao;

import com.mybus.model.City;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */
@Repository
public interface CityDAO extends PagingAndSortingRepository<City, String> {
    City findOneByName(String name);
    City findOneByNameAndState(String name, String state);
    Iterable<City> findByName(String name);
    Iterable<City> findByActive(boolean active);
    void delete(String s);

}
