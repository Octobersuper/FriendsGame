package com.zcf.framework.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    public final static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public final static String INVERSE_DATE_FORMAT = "MM/dd/yyyy";

    public final static String SHORT_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public final static String FULL_SEQ_FORMAT = "yyyyMMddHHmmssSSS";

    public final static String[] MULTI_FORMAT = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM"};

    public final static DateFormat INVERSE_DATE_FORMATER = new SimpleDateFormat(INVERSE_DATE_FORMAT);

    public final static DateFormat DEFAULT_TIME_FORMATER = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

    public final static DateFormat DEFAULT_DATE_FORMATER = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    public final static DateFormat SHORT_TIME_FORMATER = new SimpleDateFormat(SHORT_TIME_FORMAT);

    public final static String FORMAT_YYYY = "yyyy";

    public final static String FORMAT_YYYYMM = "yyyyMM";

    public final static String FORMAT_YYYYMMDD = "yyyyMMdd";

    public final static String FORMAT_MMDD = "MM-dd";

    public final static String FORMAT_YYYY_MM = "yyyy-MM";

    public final static DateFormat FORMAT_YYYY_FORMATER = new SimpleDateFormat(FORMAT_YYYY);

    public final static DateFormat FORMAT_YYYYMM_FORMATER = new SimpleDateFormat(FORMAT_YYYYMM);

    public final static DateFormat FORMAT_YYYYMMDD_FORMATER = new SimpleDateFormat(FORMAT_YYYYMMDD);

    public final static DateFormat FORMAT_MMDD_FORMATER = new SimpleDateFormat(FORMAT_MMDD);

    public static String formatMMDDDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(FORMAT_MMDD).format(date);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
    }

    public static String formatYmdDate(Date date) {
        if (date == null) {
            return null;
        }
        return FORMAT_YYYYMMDD_FORMATER.format(date);
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static Integer formatDateToInt(Date date, String format) {
        if (date == null) {
            return null;
        }
        return Integer.valueOf(new SimpleDateFormat(format).format(date));
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(DEFAULT_TIME_FORMAT).format(date);
    }

    public static String formatFullTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FULL_SEQ_FORMAT).format(date);
    }

    public static String formatShortTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(SHORT_TIME_FORMAT).format(date);
    }

    public static String formatDateNow() {
        return formatDate(DateUtils.currentDate());
    }

    public static String formatTimeNow() {
        return formatTime(DateUtils.currentDate());
    }

    public static Date parseDate(String date, DateFormat df) {
        if (date == null) {
            return null;
        }
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseTime(String date, DateFormat df) {
        if (date == null) {
            return null;
        }
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return parseDate(date, new SimpleDateFormat(DEFAULT_DATE_FORMAT));
    }

    public static Date parseTime(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return parseTime(date, new SimpleDateFormat(DEFAULT_TIME_FORMAT));
    }

    public static Date parseShortTime(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return parseTime(date, new SimpleDateFormat(SHORT_TIME_FORMAT));
    }

    public static String plusOneDay(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        DateTime dateTime = new DateTime(parseDate(date).getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String plusOneDay(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String getHumanDisplayForTimediff(Long diffMillis) {
        if (diffMillis == null) {
            return "";
        }
        long day = diffMillis / (24 * 60 * 60 * 1000);
        long hour = (diffMillis / (60 * 60 * 1000) - day * 24);
        long min = ((diffMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long se = (diffMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day + "D");
        }
        DecimalFormat df = new DecimalFormat("00");
        sb.append(df.format(hour) + ":");
        sb.append(df.format(min) + ":");
        sb.append(df.format(se));
        return sb.toString();
    }

    /**
     * 把类似2014-01-01 ~ 2014-01-30格式的单一字符串转换为两个元素数组
     */
    public static Date[] parseBetweenDates(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        date = date.replace("～", "~");
        Date[] dates = new Date[2];
        String[] values = date.split("~");
        dates[0] = parseMultiFormatDate(values[0].trim());
        dates[1] = parseMultiFormatDate(values[1].trim());
        return dates;
    }

    public static Date parseMultiFormatDate(String date) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(date, MULTI_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title:getDiffDay
     * @Description:获取日期相差天数
     * @param:@param beginDate  字符串类型开始日期
     * @param:@param endDate    字符串类型结束日期
     * @param:@return
     * @return:Long 日期相差天数
     * @author:谢
     * @thorws:
     */
    public static Long getDiffDay(String beginDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Long checkday = 0l;
        //开始结束相差天数
        try {
            checkday = (formatter.parse(endDate).getTime() - formatter.parse(beginDate).getTime()) / (1000 * 24 * 60 * 60);
        } catch (ParseException e) {

            e.printStackTrace();
            checkday = null;
        }
        return checkday;
    }

    /**
     * @Title:getDiffDay
     * @Description:获取日期相差天数
     * @param:@param beginDate Date类型开始日期
     * @param:@param endDate   Date类型结束日期
     * @param:@return
     * @return:Long 相差天数
     * @author: 谢
     * @thorws:
     */
    public static Long getDiffDay(Date beginDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strBeginDate = format.format(beginDate);

        String strEndDate = format.format(endDate);
        return getDiffDay(strBeginDate, strEndDate);
    }

    /**
     * N天之后
     *
     * @param n
     * @param date
     * @return
     */
    public static Date nDaysAfter(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + n);
        return cal.getTime();
    }

    /**
     * N天之前
     *
     * @param n
     * @param date
     * @return
     */
    public static Date nDaysAgo(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - n);
        return cal.getTime();
    }

    private static Date currentDate;

    public static Date setCurrentDate(Date date) {
        Date now = new Date();
        if (date.after(now)) {
            currentDate = now;
        } else {
            currentDate = date;
        }
        return currentDate;
    }

    /**
     * 为了便于在模拟数据程序中控制业务数据获取到的当前时间
     * 提供一个帮助类处理当前时间，为了避免误操作，只有在devMode开发模式才允许“篡改”当前时间
     *
     * @return
     */
    public static Date currentDate() {
        if (currentDate == null) {
            return new Date();
        }
        return currentDate;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        try {
            DateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取两个日期之间的日期列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getBetweenDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        try {
            List dateList = new ArrayList();
            dateList.add(startDate);
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(startDate);
            Calendar calEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calEnd.setTime(endDate);
            // 测试此日期是否在指定日期之后
            while (endDate.after(calBegin.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                dateList.add(calBegin.getTime());
            }
            return dateList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取两个时间的小时差
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static double getDifferenceNum(Date beginDate, Date endDate) {
        double result = 0;
        if (beginDate == null || endDate == null) {
            return result;
        }
        try {
            // 日期相减得到日期差X(单位:毫秒)
            long millisecond = endDate.getTime() - beginDate.getTime();
            BigDecimal val1 = new BigDecimal(millisecond);
            BigDecimal val2 = new BigDecimal(1000 * 60 * 60);
            BigDecimal val = val1.divide(val2, 1, BigDecimal.ROUND_HALF_UP);
            return val.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param beginDate
     * @param endDate
     * @param f         时间差的形式0:秒,1:分种,2:小时,3:天
     * @return
     */
    public static int getDifferenceNum(Date beginDate, Date endDate, int f) {
        int result = 0;
        if (beginDate == null || endDate == null) {
            return 0;
        }
        try {
            // 日期相减得到日期差X(单位:毫秒)
            long millisecond = endDate.getTime() - beginDate.getTime();
            /**
             * Math.abs((int)(millisecond/1000)); 绝对值 1秒 = 1000毫秒
             * millisecond/1000 --> 秒 millisecond/1000*60 - > 分钟
             * millisecond/(1000*60*60) -- > 小时 millisecond/(1000*60*60*24) -->
             * 天
             * */
            switch (f) {
                case 0: // second
                    return (int) (millisecond / 1000);
                case 1: // minute
                    return (int) (millisecond / (1000 * 60));
                case 2: // hour
                    return (int) (millisecond / (1000 * 60 * 60));
                case 3: // day
                    return (int) (millisecond / (1000 * 60 * 60 * 24));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Date plusMins(Date date, int mins) {
        DateTime dateTime = new DateTime(date.getTime());
        return dateTime.plusMinutes(mins).toDate();
    }

    /**
     * 获得当天到凌晨24点还剩下多少秒
     *
     * @return
     */
    public static Long getCurrDayExistSecond() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff / 1000;
    }


    public static String getCronTime(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        String monthStr, dayStr, hourStr, minuteStr, secondStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }
        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = String.valueOf(day);
        }
        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = String.valueOf(hour);
        }
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = String.valueOf(minute);
        }
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = String.valueOf(second);
        }
        return secondStr + " " + minuteStr + " " + hourStr + " " + dayStr + " " + monthStr + " ? " + year;
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     * @param curTime    需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }

    }

    public static Date getSecondTime(int second) {
        Calendar c = new GregorianCalendar();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.SECOND, second);//把日期往后增加 SECOND 秒.整数往后推,负数往前移动
        date = c.getTime();
        return date;
    }



    public static String dateFormat(Date date) {
        String dateStr = DateUtils.formatDate(date, DateUtils.DEFAULT_DATE_FORMAT);

        String str = DateUtils.formatDate(date, DateUtils.DEFAULT_TIME_FORMAT);

        String nowStr = DateUtils.formatDateNow();

        Date dNow = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天

        String dBefore = DateUtils.formatDate(calendar.getTime(), DateUtils.DEFAULT_DATE_FORMAT);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(date);

        if (dateStr.equals(nowStr)) {
            return "今天" + dateString;
        } else if (dateStr.equals(dBefore)) {
            return "昨天" + dateString;
        } else {
            return str;
        }

    }

    /**
     * 凌晨
     *
     * @param date
     * @return
     * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
     * 1 返回yyyy-MM-dd 23:59:59日期
     */
    public static Date weeHours(Date date, int flag) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        if (flag == 0) {
            return cal.getTime();
        } else if (flag == 1) {
            //凌晨23:59:59
            cal.setTimeInMillis(cal.getTimeInMillis() + 23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);
        }
        return cal.getTime();
    }

    public static void main(String[] args) {
        System.out.println(getCronTime(getSecondTime(10)));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // 判断当前时间是否在9点到18点之间
        boolean flag = DateUtils.isInTime("9:00-18:00", sdf.format(new Date()));
        System.out.println(flag);
        //System.out.println(DateUtils.getDifferenceNum(DateUtils.parseTime("2017-04-01 10:44:00"), DateUtils.parseTime("2017-04-01 13:45:55")));
    }

}
