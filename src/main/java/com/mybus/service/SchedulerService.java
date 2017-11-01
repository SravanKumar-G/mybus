package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.VehicleMongoDAO;
import com.mybus.model.Vehicle;
import com.mybus.util.EmailSender;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private VehicleMongoDAO vehicleMongoDAO;

    @Autowired
    private EmailSender emailSender;

    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(fixedDelay = 10000)
    public void checkExpiryDates () {
        logger.info("checking expiry date..."+ systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - buffer));
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findExpiring(calendar.getTime()).iterator());
        List<String> permitExpiring = vehicles.stream()
                .filter(v -> v.getPermitExpiry().isBefore(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

        List<String> fitnessExpiring = vehicles.stream()
                .filter(v -> v.getFitnessExpiry().isBefore(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

        List<String> authExpiring = vehicles.stream()
                .filter(v -> v.getAuthExpiry().isBefore(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

        List<String> pollutionExpiring = vehicles.stream()
                .filter(v -> v.getPollutionExpiry().isBefore(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

        List<String> insuranceExpiring = vehicles.stream()
                .filter(v -> v.getInsuranceExpiry().isBefore(calendar.getTime().getTime())).map(Vehicle::getRegNo)
                .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        if(!permitExpiring.isEmpty()) {
            builder.append("Permit expiring soon for vehicles <b>" +
                    permitExpiring.stream().collect(Collectors.joining(", "))+ "</b>");
        }

        if(!fitnessExpiring.isEmpty()) {
            builder.append("<br> Fitness expiring soon for vehicles <b>" +
                    fitnessExpiring.stream().collect(Collectors.joining(", "))+ "</b>");
        }
        if(!authExpiring.isEmpty()) {
            builder.append("<br> Authorization expiring soon for vehicles <b>" +
                    authExpiring.stream().collect(Collectors.joining(", "))+ "</b>");
        }
        if(!pollutionExpiring.isEmpty()) {
            builder.append("<br> Pollution expiring soon for vehicles <b>" +
                    pollutionExpiring.stream().collect(Collectors.joining(", "))+ "</b>");
        }
        if(!insuranceExpiring.isEmpty()) {
            builder.append("<br> Insurance expiring soon for vehicles <b>" +
                    insuranceExpiring.stream().collect(Collectors.joining(", "))+ "</b>");
        }
        if(builder.length() > 0) {
            emailSender.sendExpiringNotifications(builder.toString());
        }
    }
}
