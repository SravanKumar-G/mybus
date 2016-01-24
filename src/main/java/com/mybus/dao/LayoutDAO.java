package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mybus.model.Layout;

/**
 * Created by schanda on 01/15/16.
 */
@Repository
public interface LayoutDAO extends PagingAndSortingRepository<Layout, String> {
    Layout findOneByName(String name);
    void delete(String s);
}
