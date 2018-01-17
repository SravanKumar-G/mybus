package com.mybus.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.UserDAO;
import com.mybus.model.AbstractDocument;
import com.mybus.model.User;
import com.mybus.service.ServiceConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by srinikandula on 12/11/16.
 */
@Service
public class ServiceUtils {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> userNames = new HashMap<>();
    @PostConstruct
    public void init(){

    }
    /**
     * Method to parse date string. Boolean param indicates if the time should be should set to end of the day
     * @param dateString
     * @param endOfTheDay
     * @return
     */
    public static Date parseDate(final String dateString, boolean endOfTheDay) throws ParseException {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        DateFormat df = ServiceConstants.df;
        if (!endOfTheDay) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(dateString));
            cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
            return cal.getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(dateString));
            cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
            return cal.getTime();
        }
    }

    public void fillInUserNames(List abstractDocuments) {
        for(Object abstractDocument: abstractDocuments){
            fillInUserNames((AbstractDocument) abstractDocument);
        }
    }

    /**
     *
     * @param abstractDocuments  list of mongo documents which requires usernames to be populated with
     * @param fieldName name of the field to look up the userId from
     */
    public void fillInUserNames(List abstractDocuments, String fieldName) throws IOException {
        for(Object abstractDocument: abstractDocuments){
            fillInUserNames((AbstractDocument) abstractDocument, fieldName);
        }
    }

    /**
     *  list of mongo documents which requires usernames to be populated with
     * @param abstractDocument
     */
    public void fillInUserNames(AbstractDocument abstractDocument) {
        fillInUsername(abstractDocument, abstractDocument.getCreatedBy(), "createdBy");
        fillInUsername(abstractDocument, abstractDocument.getUpdatedBy(), "updatedBy");
    }

    public void fillInUserNames(AbstractDocument abstractDocument,String fieldName) throws IOException {
        try {
            JSONObject jsonObject = objectMapper.readValue(objectMapper.writeValueAsString(abstractDocument), JSONObject.class);
            if(jsonObject.get(fieldName) != null) {
                fillInUsername(abstractDocument, jsonObject.get(fieldName).toString(), fieldName);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    private void fillInUsername(AbstractDocument abstractDocument, String userId, String attributeName) {
        if(userId != null) {
            if(userNames.get(userId) == null) {
                User user = userDAO.findOne(userId);
                if(user != null) {
                    userNames.put(userId, user.getFullName());
                } else {
                    userNames.put(userId, "UNKNOWN");
                }
            }
            abstractDocument.getAttributes().put(attributeName, userNames.get(userId));
        }
    }

    public Query createSearchQuery(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query.get("startDate") != null) {
            match.add(Criteria.where("date").gte(ServiceConstants.df.parse(query.get("startDate").toString())));
        }
        if(query.get("endDate") != null) {
            match.add(Criteria.where("date").lte(ServiceConstants.df.parse(query.get("endDate").toString())));
        }
        if(query.get("officeId") != null) {
            List<User> officeUsers = userDAO.findByBranchOfficeId(query.get("officeId").toString());
            List<String> officeUserIds = officeUsers.stream().map(User::getId).collect(Collectors.toList());
            match.add(Criteria.where("createdBy").in(officeUserIds));
        }
        if(query.get("expenseType") != null) {
            match.add(Criteria.where("expenseType").is(query.get("expenseType").toString()));
        }
        if(query.get("userId") != null) {
            match.add(Criteria.where("createdBy").is(query.get("userId").toString()));
        }
        if(match.size() > 0) {
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return q;
    }

}
