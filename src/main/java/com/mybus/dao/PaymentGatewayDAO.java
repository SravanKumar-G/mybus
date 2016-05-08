package com.mybus.dao;

import com.mybus.model.PaymentGateway;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by skandula on 5/7/16.
 */
public interface PaymentGatewayDAO extends PagingAndSortingRepository<PaymentGateway, String> {
    PaymentGateway findByName(String name);
}
