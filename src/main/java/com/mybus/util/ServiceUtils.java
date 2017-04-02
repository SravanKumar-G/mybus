package com.mybus.util;

import com.mybus.model.Shipment;
import com.mybus.service.ServiceConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by srinikandula on 12/11/16.
 */
public class ServiceUtils {
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
            return df.parse(dateString);
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


    public static void createTimeFrameQuery(Date start, Date end, String columnName, List<Criteria> criteria) {
        if (start == null && end == null) {
            // No timeframe specified, so search over everything
            return;
        } else if (end == null) {
            criteria.add(where(columnName).gte(start));
        } else if (start == null) {
            criteria.add(where(columnName).lte(end));
        } else {
            criteria.add(where(columnName).gte(start).lte(end));
        }
    }
}
