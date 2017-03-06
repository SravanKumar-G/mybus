package com.mybus.service;

import com.mybus.dao.*;
import com.mybus.dao.impl.BranchOfficeMongoDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    public static final String BOOKING_ID = "bookingId";
    public static final String BOOKING_TRANSACTION = "Booking";

    @Autowired
    private TransactionDAO transactionDAO;

    @Async
    public void createBookingTransaction(Booking booking, Agent agent) {
        Transaction transaction = new Transaction();
        transaction.setAmount(booking.getAmount());
        transaction.getAttributes().put(BOOKING_ID, booking.getId());
        transaction.setBranchOfficeId(agent.getBranchOfficeId());
        transaction.setType(TransactionType.INCOME);
        transaction.setDescription(BOOKING_TRANSACTION);
        transactionDAO.save(transaction);
    }
}
