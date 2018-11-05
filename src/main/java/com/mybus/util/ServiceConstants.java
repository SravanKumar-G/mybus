package com.mybus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by srinikandula on 3/7/17.
 */
public class ServiceConstants {
    //public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static String df = "yyyy-MM-dd";

    public static Date parseDate(String dateValue) throws ParseException {
        return new SimpleDateFormat(df).parse(dateValue);
    }

    public static String formatDate(Date dateValue) {
        return new SimpleDateFormat(df).format(dateValue);
    }

    public static String[] EXPENSE_TYPES = {"BATTA", "DIESEL", "MATERIAL PURCHASE", "OFFICE RENT", "PC MONTHLY", "SALARY", "STATIONARY", "VEHICLE TAX", "TRIP ADVANCE", "WASHING BILL" ,"VEHICLE MAINTENANCE", "OTHER"};
}
