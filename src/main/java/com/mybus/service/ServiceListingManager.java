package com.mybus.service;

import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.ServiceListingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import com.mybus.model.ServiceExpense;
import com.mybus.model.ServiceListing;
import com.mybus.model.ServiceReportStatus;
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
    private ServiceListingMongoDAO serviceListingMongoDAO;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private AbhiBusPassengerReportService abhiBusPassengerReportService;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    /**
     * If the listing are not found and if the operator is Abhibus download them now
     * @param date
     * @return
     * @throws Exception
     */
    public Iterable<ServiceListing> getServiceListings(String date) throws Exception {
        Iterable<ServiceListing> serviceListings = serviceListingMongoDAO
                .getServiceListing(date, sessionManager.getOperatorId());
        OperatorAccount operatorAccount = operatorAccountDAO.findOne(sessionManager.getOperatorId());
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }
        if(!serviceListings.iterator().hasNext()) {
            if (operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)) {
                abhiBusPassengerReportService.getActiveServicesByDate(date);
            }
        }
        return serviceListings;
    }

}
