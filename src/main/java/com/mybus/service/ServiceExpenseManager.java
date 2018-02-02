package com.mybus.service;

import com.mybus.dao.FillingStationDAO;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.model.FillingStation;
import com.mybus.model.ServiceExpense;
import com.mybus.model.ServiceListing;
import com.mybus.model.ServiceReport;
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
public class ServiceExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseManager.class);

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private FillingStationDAO fillingStationDAO;
    @Autowired
    private ServiceListingDAO serviceListingDAO;


    public ServiceExpense save(ServiceExpense serviceExpense) {
        serviceExpense.validate();
        /*serviceExpense.setNetRealization(
                - parseFloat($scope.serviceExpense.fuelCost)
                + parseFloat($scope.serviceExpense.paidLuggage)
                + parseFloat($scope.serviceExpense.toPayLuggage)
                - parseFloat($scope.serviceExpense.driverSalary1)
                - parseFloat($scope.serviceExpense.driverSalary2)
                - parseFloat($scope.serviceExpense.cleanerSalary);
                */
        return serviceExpenseDAO.save(serviceExpense);
    }

    public ServiceExpense getServiceExpenseByServiceReportId(String serviceReportId) {
        return loadServiceInfo(serviceExpenseDAO.findByServiceReportId(serviceReportId));
    }
    public ServiceExpense getServiceExpense(String id) {
        return loadServiceInfo(serviceExpenseDAO.findOne(id));
    }
    /**
     * Load the information from servicereport
     * @param serviceExpense
     * @return
     */
    private ServiceExpense loadServiceInfo(ServiceExpense serviceExpense) {
        if(serviceExpense == null) {
            return null;
        }
        serviceExpense.validate();
        ServiceListing serviceListing = serviceListingDAO.findOne(serviceExpense.getServiceListingId());
        if(serviceListing != null) {
            serviceExpense.getAttributes().put("to", serviceListing.getDestination());
            serviceExpense.getAttributes().put("from", serviceListing.getSource());
            serviceExpense.getAttributes().put("busType", serviceListing.getBusType());
            serviceExpense.getAttributes().put("VehicleNumber", serviceListing.getVehicleRegNumber());
        }
        if(serviceExpense.getFillingStationId() != null) {
            FillingStation fillingStation = fillingStationDAO.findOne(serviceExpense.getFillingStationId());
            serviceExpense.getAttributes().put("fillingStation", fillingStation.getName());
        }
        return serviceExpense;
    }


    public List<ServiceExpense> getServiceExpenses(String journeyDate) {
        List<ServiceExpense> serviceExpenses = new ArrayList<>();
        try {
            Date start = ServiceUtils.parseDate(journeyDate, false);
            Date end = ServiceUtils.parseDate(journeyDate, true);
            List<ServiceExpense> expenses = serviceExpenseDAO.findByJourneyDateBetween(start,end);
            if(!expenses.isEmpty()) {
                for(ServiceExpense expense: expenses) {
                    serviceExpenses.add(loadServiceInfo(expense));
                }
            }
        } catch (ParseException e) {
            logger.error("Error finding service expenses ", e);
        }
        return serviceExpenses;
    }

    /**
     * Update the data with
     * @param serviceExpense
     */
    public void updateFromServiceReport(ServiceExpense serviceExpense) {
        if(serviceExpense != null) {
            ServiceExpense savedExpense = serviceExpenseDAO.findOne(serviceExpense.getId());
            savedExpense.setFuelQuantity(serviceExpense.getFuelQuantity());
            savedExpense.setFuelRate(serviceExpense.getFuelRate());
            savedExpense.setFuelCost(serviceExpense.getFuelCost());
            savedExpense.setDriverSalary1(serviceExpense.getDriverSalary1());
            savedExpense.setDriverSalary2(serviceExpense.getDriverSalary2());
            savedExpense.setCleanerSalary(serviceExpense.getCleanerSalary());
            savedExpense.setPaidLuggage(serviceExpense.getPaidLuggage());
            savedExpense.setToPayLuggage(serviceExpense.getToPayLuggage());
            savedExpense.setNetRealization(serviceExpense.getNetRealization());
            serviceExpenseDAO.save(savedExpense);
        }

    }
}
