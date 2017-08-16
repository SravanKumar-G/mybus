package com.mybus.service;

import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.model.ServiceExpense;
import com.mybus.model.ServiceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseManager.class);

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    public ServiceExpense save(ServiceExpense serviceExpense) {
        serviceExpense.validate();
        return serviceExpenseDAO.save(serviceExpense);
    }

    public ServiceExpense getServiceExpense(String serviceExpenseId) {
        return loadServiceInfo(serviceExpenseDAO.findOne(serviceExpenseId));

    }
    /**
     * Load the information from servicereport
     * @param serviceExpense
     * @return
     */
    private ServiceExpense loadServiceInfo(ServiceExpense serviceExpense) {
        serviceExpense.validate();
        ServiceReport serviceReport = serviceReportDAO.findOne(serviceExpense.getServiceReportId());
        serviceExpense.getAttributes().put("to", serviceReport.getDestination());
        serviceExpense.getAttributes().put("from", serviceReport.getSource());
        serviceExpense.getAttributes().put("busType", serviceReport.getBusType());
        serviceExpense.getAttributes().put("busType", serviceReport.getVehicleRegNumber());
        return serviceExpense;
    }

    public List<ServiceExpense> getServiceExpenses(String journeyDate) {
        List<ServiceExpense> serviceExpenses = new ArrayList<>();
        try {
            Iterable<ServiceExpense> expenses = serviceExpenseDAO
                    .findByJourneyDate(ServiceConstants.df.parse(journeyDate));
            for(ServiceExpense expense: expenses) {
                serviceExpenses.add(loadServiceInfo(expense));
            }
        } catch (ParseException e) {
            logger.error("Error finding service expenses ", e);
        }
        return serviceExpenses;
    }
}
