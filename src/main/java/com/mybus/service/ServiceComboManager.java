package com.mybus.service;

import com.mybus.dao.ServiceComboDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.ServiceCombo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by srinikandula on 3/10/17.
 */
@Service
public class ServiceComboManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceComboManager.class);

    @Autowired
    private ServiceComboDAO serviceComboDAO;

    public Map<String, String[]> getServiceComboMappings() {
        Map<String, String[]> mappings = new HashMap<>();
        for( ServiceCombo serviceCombo: serviceComboDAO.findByActive(true)) {
            mappings.put(serviceCombo.getServiceNumber(), serviceCombo.getComboNumbers().split(","));
        }
        return mappings;
    }

    public List<String> getServiceComboNumbers() {
        List<String> list = new ArrayList<>();
        for(ServiceCombo serviceCombo:serviceComboDAO.findByActive(true)) {
            list.add(serviceCombo.getServiceNumber());
            if(serviceCombo.getComboNumbers() != null) {
                String[] names = serviceCombo.getComboNumbers().split(",");
                list.addAll(Arrays.asList(names));
            }
        }
        return list;
    }

    public ServiceCombo update(ServiceCombo serviceCombo) {
        if(serviceCombo.getId() == null) {
            throw new BadRequestException("ComboId can not be null");
        }
        ServiceCombo savedCombo = serviceComboDAO.findOne(serviceCombo.getId());
        try {
            savedCombo.merge(serviceCombo);
            return serviceComboDAO.save(savedCombo);
        } catch (Exception e) {
            LOGGER.error("Error updating the Route ", e);
            throw new RuntimeException(e);
        }
    }
}
