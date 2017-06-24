package com.keyo.cardapio.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by renarosantos on 09/08/16.
 */
public class CalendarDateUtils {

    public static final String VIEW_DATE_FORMAT = "dd/MM/yyyy";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final int MAX_HOURS = 23;
    private static final int MAX_MINUTES = 59;
    private static final int MAX_SECONDS = 59;
    private static final int MIN_MINUTES = 0;
    private static final int MIN_SECONDS = 1;
    private static final int MIN_HOURS = 0;
    private static final int DECEMBER_MONTH = 12;
    private static final int LAST_MONTH_INDEX = 11;
    private static final int DATE_FORMAT_SIZE = 10;
    private static final int DEFAULT_YEAR = 2016;
    private static final int MIDDAY = 12;
    private static final int MAX_HOUR_OF_DAY = 23;
    private static final int MAX_MINUTE_AND_SECOND = 59;
    private static final int YEAR_SIZE = 4;

    public Date getTodayAtFirstTime() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, MIN_HOURS);
        calendar.set(Calendar.MINUTE, MIN_MINUTES);
        calendar.set(Calendar.SECOND, MIN_SECONDS);
        return calendar.getTime();
    }

    public Date getTodayAtLastTime() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, MAX_HOURS);
        calendar.set(Calendar.MINUTE, MAX_MINUTES);
        calendar.set(Calendar.SECOND, MAX_SECONDS);
        return calendar.getTime();
    }

    public Date getDateAtLastTime(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, MAX_HOURS);
        calendar.set(Calendar.MINUTE, MAX_MINUTES);
        calendar.set(Calendar.SECOND, MAX_SECONDS);
        return calendar.getTime();
    }

    public boolean isDateBeforeOneYear(final Date menstruationLastDate) {
        Calendar lastYearCalendar = Calendar.getInstance();
        lastYearCalendar.setTimeZone(TimeZone.getDefault());
        final int year = lastYearCalendar.get(Calendar.YEAR);
        lastYearCalendar.set(Calendar.YEAR, year - 1);
        Calendar todaysCalendar = Calendar.getInstance();
        todaysCalendar.setTime(menstruationLastDate);
        return todaysCalendar.compareTo(lastYearCalendar) < 0;
    }

    public String replaceDateDay(final String baseDate, final int finalDay) {
        if (isCorrectDateSize(baseDate)) {
            final String baseString = baseDate.substring(0, DATE_FORMAT_SIZE - 2);
            return baseString.concat(String.format(Locale.US, "%02d", finalDay));
        } else {
            return baseDate;
        }
    }

    public String getStringDate(@NonNull final Date baseDate) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        return format.format(baseDate);
    }

    public Date getDate(@NonNull final String baseDate) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        try {
            return format.parse(baseDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @return the getDate now, following the patter yyyy-MM-dd
     */
    public String date() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        return format1.format(cal.getTime());
    }

    /**
     * @param date1
     *         getDate following pattern yyyy-MM-dd
     * @param date2
     *         getDate following pattern yyyy-MM-dd
     *
     * @return true is date1 and date2 are within same month
     */
    public boolean isDateWithinSameMonth(final String date1, final String date2) {
        if (isCorrectDateSize(date1) && isCorrectDateSize(date2)) {
            final String month1 = date1.substring(0, 7);
            final String month2 = date2.substring(0, 7);
            return month1.equals(month2);
        }
        return false;
    }

    public boolean isSameMonth(@NonNull final Date date1, @NonNull final Date date2) {
        final Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTime(date1);
        final Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }

    private boolean isCorrectDateSize(@Nullable final String date) {
        return date != null && date.length() == DATE_FORMAT_SIZE;
    }

    public String dateInFirstDay(@NonNull final String baseDate) {
        if (isCorrectDateSize(baseDate)) {
            final int year = Integer.parseInt(baseDate.substring(0, 4));
            final int month = Integer.parseInt(baseDate.substring(5, 7)) - 1; //months are
            // required to be
            // zero based, so JAN = 0
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            return format.format(cal.getTime());
        } else {
            return baseDate;
        }
    }

    /**
     * @param date
     *         to compare
     *
     * @return true if getDate is after today
     */
    public boolean isAfterThanToday(@NonNull final Date date) {
        final Calendar todayCalendar = Calendar.getInstance(TimeZone.getDefault());
        final Calendar inputCalendar = Calendar.getInstance(TimeZone.getDefault());
        inputCalendar.setTime(date);
        inputCalendar.set(Calendar.HOUR, 0);
        inputCalendar.set(Calendar.MINUTE, 0);
        inputCalendar.set(Calendar.SECOND, 0);
        return inputCalendar.after(todayCalendar);
    }

    public String getLastDateOfMonth(@NonNull final String baseDate) {
        if (isCorrectDateSize(baseDate)) {
            final int year = Integer.parseInt(baseDate.substring(0, 4));
            final int month = Integer.parseInt(baseDate.substring(5, 7)); //months are required
            // to be zero
            // based, so JAN = 0
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, month - 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(year, month - 1, lastDay);
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            return format.format(cal.getTime());
        } else {
            return baseDate;
        }
    }

    /*
    * Expect a baseDate string as yyyy-MM-dd, returns the base getDate + 1 month at it's first day,
    * following the
    * pattern yyyy-MM-dd
    * */
    public String getNextMonthFirstDay(@NonNull final String baseDate) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            final int year = Integer.parseInt(baseDate.substring(0, 4));
            final int month = Integer.parseInt(baseDate.substring(5, 7)); //months are required
            // to be zero
            // based, so JAN = 0
            final Calendar cal = Calendar.getInstance();
            if (month == DECEMBER_MONTH) {
                cal.set(year + 1, 0, 1);
            } else {
                cal.set(year, month, 1);
            }
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            return format.format(cal.getTime());
        } else {
            return baseDate;
        }
    }

    /*
    * Expect a baseDate string as yyyy-MM-dd, returns the base getDate - 1 month, following the
    * pattern yyyy-MM-dd
    * */
    public String getPreviousMonthFirstDay(@NonNull final String baseDate) {
        if (isCorrectDateSize(baseDate)) {
            final int year = Integer.parseInt(baseDate.substring(0, 4));
            final int month = Integer.parseInt(baseDate.substring(5, 7)) - 1;//months are
            // required to be
            // zero based, so JAN = 0
            final Calendar cal = Calendar.getInstance();
            if (month == 0) {
                cal.set(year - 1, LAST_MONTH_INDEX, 1);
            } else {
                cal.set(year, month - 1, 1);
            }
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            return format.format(cal.getTime());
        } else {
            return baseDate;
        }
    }

    /*
    * Expect a baseDate string as yyyy-MM-dd, returns the base getDate - 1 month, following the
    * pattern yyyy-MM-dd
    * */
    public Date getPreviousMonthFirstDay(@NonNull final Date baseDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        int month = cal.get(Calendar.MONTH);
        if (month == Calendar.JANUARY) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        } else {
            cal.set(Calendar.MONTH, month - 1);
        }

        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public Date getNextMonthFirstDay(@NonNull final Date baseDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        int month = cal.get(Calendar.MONTH);
        if (month == Calendar.DECEMBER) {
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        } else {
            cal.set(Calendar.MONTH, month + 1);
        }

        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public int getDay(@NonNull final String date) {
        if (date.isEmpty() || date.length() < DATE_FORMAT_SIZE) {
            return 1;
        } else {
            final String dayValue = date.substring(8, 10);
            try {
                return Integer.parseInt(dayValue);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
    }

    /**
     * @param baseDate
     *         following format yyyy-MM-dd
     *
     * @return 1 for SUNDAY, 2 for MONDAY and so on.
     */
    public int getFirstDayOfMonth(@NonNull final String baseDate) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            final int year = Integer.parseInt(baseDate.substring(0, 4));
            final int month = Integer.parseInt(baseDate.substring(5, 7)) - 1; //months are
            // required to be
            // zero based, so JAN = 0
            final int day = 1;
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            return cal.get(Calendar.DAY_OF_WEEK);
        } else {
            return 1;
        }
    }

    /**
     * @param baseDate
     *         following format yyyy-MM-dd
     *
     * @return the last day of that month
     */
    public int getLastDayOfMonth(@NonNull final String baseDate) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            final int year = Integer.parseInt(baseDate.substring(0, YEAR_SIZE));
            final int month = Integer.parseInt(baseDate.substring(5, 7)) - 1; //months are
            // required to be
            // zero based, so JAN = 0
            final int day = 1;
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            return 1;
        }
    }

    public Date getLastDateOfMonth(@NonNull final Date baseDate) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(baseDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, MAX_HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, MAX_MINUTE_AND_SECOND);
        calendar.set(Calendar.SECOND, MAX_MINUTE_AND_SECOND);
        return calendar.getTime();
    }

    public Date getFirstDateOfMonth(@NonNull final Date baseDate) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(baseDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public String sumDays(final String baseDate, final int daysAmount) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            final int year = Integer.parseInt(baseDate.substring(0, YEAR_SIZE));
            final int month = Integer.parseInt(baseDate.substring(5, 7)) - 1; //months are
            // required to be
            // zero based, so JAN = 0
            final int day = Integer.parseInt(baseDate.substring(8, 10));
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            cal.add(Calendar.DATE, daysAmount);
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            return format.format(cal.getTime());
        } else {
            return baseDate;
        }
    }

    public int getYear(final String baseDate) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            try {
                return Integer.parseInt(baseDate.substring(0, YEAR_SIZE));
            } catch (NumberFormatException e) {
                Log.e("getDate error", "daate format error", e);
                throw new IllegalArgumentException("invalid getDate format");
            }
        } else {
            return DEFAULT_YEAR;
        }
    }

    public int getMonth(final String baseDate) {
        if (baseDate.length() >= DATE_FORMAT_SIZE) {
            try {
                return Integer.parseInt(baseDate.substring(5, 7)) - 1;
            } catch (NumberFormatException e) {
                Log.e("getDate error", "daate format error", e);
                throw new IllegalArgumentException("invalid getDate format");
            }
        } else {
            return DEFAULT_YEAR;
        }
    }

    /**
     * @param date1
     *         TO BE COMPARED
     * @param date2
     *         second date to be compared
     *
     * @return true if date1 and date2 are not in the same month AND date1 is before date2
     */
    public boolean isBeforeMonth(final Date date1, final Date date2) {
        final Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTime(date1);
        final Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) < calendar2.get(Calendar.YEAR) ||
               (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) < calendar2.get(Calendar.MONTH));
    }

    private Calendar normalizedDate(@NonNull final Calendar calendar) {
        final Calendar output = Calendar.getInstance();
        output.setTime(calendar.getTime());
        output.set(Calendar.HOUR_OF_DAY, MIDDAY);
        output.set(Calendar.MINUTE, MIN_MINUTES);
        output.set(Calendar.SECOND, MIN_SECONDS);
        output.set(Calendar.MILLISECOND, MIN_SECONDS);
        return output;
    }

    public boolean isSameDay(final Date date1, final Date date2) {
        final Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        final Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public Date sumDaysToDate(final Date date, final int days) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date getThisWeekMonday() {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date());

        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
