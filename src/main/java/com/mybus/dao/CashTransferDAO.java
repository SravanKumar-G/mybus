package com.mybus.dao;

import com.mybus.model.CashTransfer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by srinikandula on 3/19/17.
 */
public interface CashTransferDAO extends PagingAndSortingRepository<CashTransfer, String> {

}
