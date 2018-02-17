package com.mybus.service;

import com.mybus.dao.FillingStationDAO;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.dao.impl.ServiceExpenseMongoDAO;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

@Service
public class ServiceExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseManager.class);

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceExpenseMongoDAO serviceExpenseMongoDAO;

    @Autowired
    private FillingStationDAO fillingStationDAO;
    @Autowired
    private ServiceListingDAO serviceListingDAO;

    private Map<String, FillingStation> fillingStationMap = new HashMap<>();

    @PostConstruct
    public void init(){
        Iterable<FillingStation> fillingStations = fillingStationDAO.findAll();
        fillingStations.forEach(fillingStation -> {
            fillingStationMap.put(fillingStation.getId(), fillingStation);
        });
    }

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
                    ServiceExpense loadDataExpense = loadServiceInfo(expense);
                    loadFillingStationInfo(loadDataExpense);
                    serviceExpenses.add(loadDataExpense);
                }
            }
        } catch (ParseException e) {
            logger.error("Error finding service expenses ", e);
        }
        return serviceExpenses;
    }


    private void loadFillingStationInfo(ServiceExpense serviceExpense) {
        serviceExpense.getAttributes().put("fillingStationName",
                fillingStationMap.get(serviceExpense.getFillingStationId()).getName());
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

    public List<ServiceExpense> findServiceExpenses(JSONObject query, Pageable pageable) throws ParseException {
        List<ServiceExpense>  serviceExpenses = serviceExpenseMongoDAO.searchServiceExpenses(query, pageable);
        for(ServiceExpense expense: serviceExpenses) {
            loadServiceInfo(expense);
            loadFillingStationInfo(expense);
        }
        return serviceExpenses;
    }
}
