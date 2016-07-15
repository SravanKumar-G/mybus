package com.mybus.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mybus.model.Payment;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Repository
public interface PaymentDAO extends PagingAndSortingRepository<Payment, String> {

}