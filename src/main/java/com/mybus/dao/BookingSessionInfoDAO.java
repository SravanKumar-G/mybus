package com.mybus.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mybus.service.BookingSessionInfo;

public interface BookingSessionInfoDAO extends PagingAndSortingRepository<BookingSessionInfo, Serializable> {

}
