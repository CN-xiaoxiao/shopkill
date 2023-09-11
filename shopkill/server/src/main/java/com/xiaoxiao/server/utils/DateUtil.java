package com.xiaoxiao.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil{
    public static Date get(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(pattern);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isOrder(Date date1, Date date2) {


        if (date1 == null || date2 == null) {
            return false;
        }

        return date1.before(date2);
    }
}
