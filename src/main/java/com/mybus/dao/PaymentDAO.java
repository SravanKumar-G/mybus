package com.mybus.dao;

import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */

@Repository
public interface PaymentDAO extends PagingAndSortingRepository<Payment, String> {
    Iterable<Payment> findByType(PaymentType type);
    Iterable<Payment> findByFormId(String serviceId);
}
