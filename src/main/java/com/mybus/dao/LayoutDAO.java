package com.mybus.dao;

import com.mybus.model.City;
import com.mybus.model.Layout;
import com.mybus.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by schanda on 01/15/16.
 */
@Repository
public interface LayoutDAO extends PagingAndSortingRepository<Layout, String> {
    Layout findOneByName(String name);
    void delete(String s);
}
