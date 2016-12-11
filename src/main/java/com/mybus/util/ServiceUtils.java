package com.mybus.util;

import com.mybus.model.Shipment;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        DateFormat df = Shipment.dateTimeFormat;
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
}
