package com.awspaas.user.apps.finance.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    public static final String DATETIME_DFT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_DFT_PATTERN = "yyyy-MM-dd";

    public static String getCurrentDate() {
        return getCurrentDate(new Date(), DATE_DFT_PATTERN);
    }

    public static String getCurrentDate(String pattern) {
        return getCurrentDate(new Date(), pattern);
    }

    public static String getCurrentDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String getCurrentDateTime() {
        return format(new Date(), DATETIME_DFT_PATTERN);
    }

    public static String getCurrentDateTime(String pattern) {
        return format(new Date(), pattern);
    }

    public static String format(long datetime) {
        return format(new Date(datetime));
    }

    public static String format(long datetime, String pattern) {
        return format(new Date(datetime), pattern);
    }

    public static String format(Date date) {
        return format(date, DATE_DFT_PATTERN);
    }

    public static String formatdate(Date date) {
        return format(date, DATETIME_DFT_PATTERN);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(String dateStr) {
        return format(parse(dateStr, DATETIME_DFT_PATTERN), DATETIME_DFT_PATTERN);
    }

    public static String formatdate(String dateStr) {
        return format(parse(dateStr, DATE_DFT_PATTERN), DATE_DFT_PATTERN);
    }

    public static String format(String dateStr, String pattern) {
        return format(parse(dateStr, DATETIME_DFT_PATTERN), pattern);
    }

    public static String format(String dateStr, String orgPattern, String pattern) {
        return format(parse(dateStr, orgPattern), pattern);
    }

    public static Date parse(String dateStr) {
        return parse(dateStr, DATE_DFT_PATTERN);
    }

    public static Date parsetime(String dateStr) {
        return parse(dateStr, DATETIME_DFT_PATTERN);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parse(Date date) {
        return parse(date, DATE_DFT_PATTERN);
    }

    public static Date parse(Date date, String pattern) {
        return parse(format(date, DATETIME_DFT_PATTERN), pattern);
    }

    public static Date parse(long datetime) {
        return new Date(datetime);
    }

    public static Date parse(long datetime, String pattern) {
        return parse(new Date(datetime), pattern);
    }

    public static String parseStr(String dateStr, String pattern) {
        return format(parse(dateStr), pattern);
    }

    public static Date getFirstOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(5, 1);
        return cal.getTime();
    }

    public static Date getFirstOfMonth(long datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime);
        cal.set(5, 1);
        return cal.getTime();
    }

    public static Date getLastOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(5, 1);
        cal.add(2, 1);
        cal.add(5, -1);
        return cal.getTime();
    }

    public static Date getLastOfMonth(long datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime);
        cal.set(5, 1);
        cal.add(2, 1);
        cal.add(5, -1);
        return cal.getTime();
    }

    public static Date addDate(Date date, int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

    public static String addDateOfDay(Date date, int amount) {
        return format(addDate(new Date(), 5, amount));
    }

    public static String beforeDateOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime()).toString();
    }

    public static String afterDateOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, 1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime()).toString();
    }

    public static String beforeDateOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(1, -1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime()).toString();
    }

    public static String beforeDateOfYear(Date date) {
        Calendar.getInstance().add(1, -1);
        return new SimpleDateFormat("yyyyMMdd").format(date).toString();
    }

    public static String afterDateOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(1, 1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime()).toString();
    }

    public static String afterDateOfYear(Date date) {
        Calendar.getInstance().add(1, 1);
        return new SimpleDateFormat("yyyyMMdd").format(date).toString();
    }

    public static String currentDate() {
        return getCurrentDate("yyyyMMdd");
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(7);
    }

    public static int getDayOfWeekToChina(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekday = calendar.get(7);
        if (weekday == 1) {
            return 7;
        }
        return weekday - 1;
    }

    public static Date alterDate(Date date, int CalendarTyep, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(CalendarTyep, num);
        return calendar.getTime();
    }

    public static String alterDateToString(Date date, int CalendarTyep, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(CalendarTyep, num);
        return new SimpleDateFormat(DATE_DFT_PATTERN).format(calendar.getTime());
    }

    public static int differentDaysByMillisecond(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 86400000);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(format(alterDateToString(parse("2017-08-04"), 5, i)));
        }
        long time = parse(new Date()).getTime();
        System.out.println(getCurrentDate(DATETIME_DFT_PATTERN));
    }

    public static Timestamp getSysTimeStamp() {
        String format = new SimpleDateFormat(DATETIME_DFT_PATTERN).format(new Timestamp(System.currentTimeMillis()));
        return Timestamp.valueOf(new SimpleDateFormat(DATETIME_DFT_PATTERN).format(new Date()));
    }

    public static Timestamp getSystemTime() {
        return Timestamp.valueOf(new SimpleDateFormat(DATETIME_DFT_PATTERN).format(new Date()));
    }

    public static String getTimeNotms() {
        return new SimpleDateFormat(DATETIME_DFT_PATTERN).format(new Date());
    }

    public static List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
        List<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(parse(minDate, "yyyy-MM"));
        min.set(min.get(1), min.get(2), 1);
        max.setTime(parse(maxDate, "yyyy-MM"));
        max.set(max.get(1), max.get(2), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(formatdate(curr.getTime()));
            curr.add(2, 1);
        }
        return result;
    }
}