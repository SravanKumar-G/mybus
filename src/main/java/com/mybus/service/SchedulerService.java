package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.VehicleMongoDAO;
import com.mybus.model.Vehicle;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private VehicleMongoDAO vehicleMongoDAO;

    @Scheduled(cron = "0 0 0 * *")
    public void checkExpiryDates () {
        logger.info("checking expiry date..."+ systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - buffer));
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findExpiring(calendar.getTime()).iterator());
        List<String> permitExpiring = vehicles.stream()
                .filter(v -> v.getPermitExpiry().isAfter(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());
        List<String> fitnessExpiring = vehicles.stream()
                .filter(v -> v.getFitnessExpiry().isAfter(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

    }
}
