package com.mybus.service;

import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.model.ServiceExpense;
import com.mybus.model.ServiceListing;
import com.mybus.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServiceListingManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceListingManager.class);

    @Autowired
    private ServiceListingDAO serviceListingDAO;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private AbhiBusPassengerReportService reportService;

    public Iterable<ServiceListing> getServiceListings(String date) throws Exception {
        Date listingDate = ServiceUtils.parseDate(date, false);
        Iterable<ServiceListing> serviceListings = serviceListingDAO.findByJourneyDateAndOperatorId
                (listingDate, sessionManager.getOperatorId());
        if(!serviceListings.iterator().hasNext()) {
            serviceListings = reportService.getActiveServicesByDate(date);
        }
        return serviceListings;
    }

}
