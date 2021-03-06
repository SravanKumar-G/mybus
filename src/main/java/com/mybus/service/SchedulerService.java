package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.dao.impl.VehicleMongoDAO;
import com.mybus.model.OperatorAccount;
import com.mybus.model.ServiceReport;
import com.mybus.model.Vehicle;
import com.mybus.util.EmailSender;
import com.mybus.util.ServiceConstants;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private VelocityEngineService velocityEngineService;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;

    @Autowired
    private ServiceReportsManager serviceReportsManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Scheduled(cron = "0 0 3 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void checkExpiryDates () {
        logger.info("checking expiry date..." + systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - buffer));
        List<OperatorAccount> operatorAccounts = IteratorUtils.toList(operatorAccountDAO.findAll().iterator());
        for(OperatorAccount operatorAccount:operatorAccounts) {
            if (operatorAccount.getEmail() != null) {
                List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findExpiring(calendar.getTime()).iterator());
                if (!vehicles.isEmpty()) {
                    Map<String, Object> context = new HashMap<>();
                    context.put("permitExpiring", vehicles.stream().filter(v -> v.getPermitExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("fitnessExpiring", vehicles.stream().filter(v -> v.getFitnessExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("authExpiring", vehicles.stream().filter(v -> v.getAuthExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("pollutionExpiring", vehicles.stream().filter(v -> v.getPollutionExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("insuranceExpiring", vehicles.stream().filter(v -> v.getInsuranceExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    String content = velocityEngineService.trasnform(context, VelocityEngineService.EXPIRING_DOCUMENTS_TEMPLATE);
                    logger.info("Sending email for notifying expiring documents ...");
                    emailSender.sendExpiringNotifications(content, operatorAccount.getEmail());
                }
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void checkServiceReportsReview () throws ParseException {
        List<OperatorAccount> operatorAccounts = IteratorUtils.toList(operatorAccountDAO.findAll().iterator());
        for(OperatorAccount operatorAccount:operatorAccounts){
            if(operatorAccount.getEmail() != null) {
                Map<String, Object> context = new HashMap<>();
                List<ServiceReport> reports = IteratorUtils.toList(serviceReportMongoDAO.findPendingReports(null, operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("pendingReports", reports);
                }
                reports = IteratorUtils.toList(serviceReportMongoDAO.findReportsToBeReviewed(null, operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("reportsToBeReviewed", reports);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -3);
                reports = IteratorUtils.toList(serviceReportMongoDAO.findHaltedReports(calendar.getTime(), operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("haltedReports", reports);
                }
                if(!context.isEmpty()) {
                    String content = velocityEngineService.trasnform(context, VelocityEngineService.PENDING_SERVICEREPORTS_TEMPLATE);
                    emailSender.sendServiceReportsToBeReviewed(content, operatorAccount.getEmail());
                }
            }
        }
    }

   // @Scheduled(cron = "0 0 1 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void downloadServiceReports () throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        serviceReportsManager.downloadReports(ServiceConstants.formatDate(calendar.getTime()));
    }
}
