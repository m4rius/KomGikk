package com.marius.komgikk.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import javax.validation.constraints.NotNull;

public class DateUtil {

    private static DateTimeZone timeZone = DateTimeZone.forID("Europe/Oslo");

    public static DateTime now() {
        return DateTime.now(timeZone);
    }

    public static DateTime getStartOfWeek(int week, int year) {
        return new DateTime(timeZone)
                .withYear(year)
                .withWeekOfWeekyear(week)
                .withDayOfWeek(1)
                .withMillisOfDay(0);
    }

    public static DateTime getEndOfWeek(int week, int year) {
        return new DateTime(timeZone)
                .withYear(year)
                .withWeekOfWeekyear(week)
                .withDayOfWeek(7)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withMillisOfSecond(999);
    }

    public static DateTime toMidnight(@NotNull DateTime dateTime) {
        return dateTime
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    public static DateTime at(@NotNull DateTime dateTime, int hour, int minutes) {
        return dateTime.withHourOfDay(hour).withMinuteOfHour(minutes).withSecondOfMinute(0).withMillisOfSecond(0);
    }

    public static DateTime parse(String time) {
        DateTime dateTime = DateTime.parse(time, DateTimeFormat.forPattern("dd.MM.yyyy HH:mm"));
        dateTime = dateTime.withZoneRetainFields(timeZone);
        return dateTime;
    }

    public static DateTime normalize(DateTime dateTime) {
        int minuteOfDay = dateTime.getMinuteOfHour();
        int normalizedMin = (minuteOfDay/15) * 15;
        if (minuteOfDay - normalizedMin > 6) {
            normalizedMin = normalizedMin + 15;
        }
        return dateTime.withMinuteOfHour(0).plusMinutes(normalizedMin); //might be 60 min
    }
}
