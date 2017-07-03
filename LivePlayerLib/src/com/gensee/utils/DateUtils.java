package com.gensee.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final ThreadLocal<DateFormat> ISO8601Format = new ThreadLocal() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssZ", Locale.US);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static final SimpleDateFormat monthFormater = new SimpleDateFormat("MM月dd日 HH:mm");
    private static final SimpleDateFormat hourFormater = new SimpleDateFormat("HH:mm");

    private DateUtils() {
        throw new AssertionError();
    }

    public static String timeFormate(String time) {
        String formateTime = "";
        if (TextUtils.isEmpty(time.trim())) {
            return formateTime;
        } else {
            Date date = new Date();
            SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                date = simple1.parse(time);
            } catch (ParseException var5) {
                var5.printStackTrace();
            }

            formateTime = javaDateToTimeSpan(date);
            return formateTime;
        }
    }

    public static String javaDateToTimeSpan(Date date) {
        if (date == null) {
            return "";
        } else {
            long passedTime = date.getTime();
            Date curDate = new Date(System.currentTimeMillis());
            long currentTime = curDate.getTime();

            SimpleDateFormat secondsSince1;
            try {
                if (isYeaterday(date, (Date) null)) {
                    secondsSince1 = new SimpleDateFormat("昨天 HH:mm");
                    return secondsSince1.format(date);
                }
            } catch (Exception var16) {
                var16.printStackTrace();
            }

            if (date.getYear() < curDate.getYear()) {
                secondsSince1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return secondsSince1.format(date);
            } else {
                long secondsSince = (currentTime - passedTime) / 1000L;
                if (secondsSince < 60L) {
                    return "刚刚";
                } else {
                    long minutesSince = secondsSince / 60L;
                    if (minutesSince < 60L) {
                        return Long.toString(minutesSince) + "分钟前";
                    } else {
                        long hoursSince = minutesSince / 60L;
                        if (hoursSince < 24L) {
                            return Long.toString(hoursSince) + "小时前";
                        } else {
                            long daysSince = hoursSince / 24L;
                            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                            if (daysSince < 365L) {
                                return format.format(date);
                            } else {
                                SimpleDateFormat format_no_currentyear = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                return format_no_currentyear.format(date);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isYeaterday(Date oldTime, Date newTime) throws ParseException {
        if (newTime == null) {
            newTime = new Date();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        return today.getTime() - oldTime.getTime() > 0L && today.getTime() - oldTime.getTime() <= 86400000L ? true : (today.getTime() - oldTime.getTime() <= 0L ? false : false);
    }

    public static Date iso8601ToJavaDate(String strDate) {
        try {
            DateFormat e = (DateFormat) ISO8601Format.get();
            return e.parse(strDate);
        } catch (ParseException var2) {
            return null;
        }
    }

    public static String javaDateToIso8601(Date date) {
        if (date == null) {
            return "";
        } else {
            DateFormat formatter = (DateFormat) ISO8601Format.get();
            return formatter.format(date);
        }
    }

    public static Date nowUTC() {
        Date dateTimeNow = new Date();
        return localDateToUTC(dateTimeNow);
    }

    public static Date localDateToUTC(Date dtLocal) {
        if (dtLocal == null) {
            return null;
        } else {
            TimeZone tz = TimeZone.getDefault();
            int currentOffsetFromUTC = tz.getRawOffset() + (tz.inDaylightTime(dtLocal) ? tz.getDSTSavings() : 0);
            return new Date(dtLocal.getTime() - (long) currentOffsetFromUTC);
        }
    }

    public static int minutesBetween(Date dt1, Date dt2) {
        long msDiff = millisecondsBetween(dt1, dt2);
        return msDiff == 0L ? 0 : (int) (msDiff / 60000L);
    }

    public static int secondsBetween(Date dt1, Date dt2) {
        long msDiff = millisecondsBetween(dt1, dt2);
        return msDiff == 0L ? 0 : (int) (msDiff / 1000L);
    }

    public static long millisecondsBetween(Date dt1, Date dt2) {
        return dt1 != null && dt2 != null ? Math.abs(dt1.getTime() - dt2.getTime()) : 0L;
    }

    public static long iso8601ToTimestamp(String strDate) {
        Date date = iso8601ToJavaDate(strDate);
        return date == null ? 0L : date.getTime() / 1000L;
    }

    public static Date timestampToDate(long timeStamp) {
        return new Date(timeStamp * 1000L);
    }

    public static String timestampToIso8601Str(long timestamp) {
        return javaDateToIso8601(timestampToDate(timestamp));
    }

    public static String timestampToTimeSpan(long timeStamp) {
        Date dtGmt = timestampToDate(timeStamp);
        return javaDateToTimeSpan(dtGmt);
    }

    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = ((SimpleDateFormat) dateFormater2.get()).format(today);
            String timeDate = ((SimpleDateFormat) dateFormater2.get()).format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }

        return b;
    }

    public static Date toDate(String sdate) {
        try {
            return ((SimpleDateFormat) dateFormater.get()).parse(sdate);
        } catch (ParseException var2) {
            return null;
        }
    }

    /**
     * 得到昨天的日期
     *
     * @return
     */
    public static String getYestoryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yestoday = sdf.format(calendar.getTime());
        return yestoday;
    }

    /**
     * 得到今天的日期
     *
     * @return
     */
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 时间戳转化为时间格式
     *
     * @param timeStamp
     * @return
     */
    public static String timeStampToStr(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(timeStamp * 1000);
        return date;
    }

    public static String getMonthDate(Long stamp) {

        return monthFormater.format(stamp);
    }

    public static String getHourDate(Long stamp) {

        return hourFormater.format(stamp);
    }

    public static String timeFormate(Long timeStamp) {

        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return timeStampToStr(timeStamp);
        }
    }
}