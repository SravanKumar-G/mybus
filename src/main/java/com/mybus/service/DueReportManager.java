package com.mybus.service;

import com.mybus.dao.*;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.model.*;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class DueReportManager {
    private static final Logger logger = LoggerFactory.getLogger(DueReportManager.class);

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private AbhiBusPassengerReportService reportService;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private ExpenseDAO expenseDAO;

    @Autowired
    private ServiceFormDAO serviceFormDAO;

    @Autowired
    private BookingTypeManager bookingTypeManager;


}
