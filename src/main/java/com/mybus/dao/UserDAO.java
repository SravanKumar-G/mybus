package com.mybus.dao;

import com.mybus.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */
@Repository
public interface UserDAO extends PagingAndSortingRepository<User, String> {
    User findOneByUserName(String username);
}
