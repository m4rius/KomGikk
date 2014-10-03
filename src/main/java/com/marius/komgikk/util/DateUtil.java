package com.marius.komgikk.util;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class DateUtil {

    public static DateTime getStartOfWeek(int week, int year) {
        return new DateTime()
                .withYear(year)
                .withWeekOfWeekyear(week)
                .withDayOfWeek(1)
                .withMillisOfDay(0);
    }

    public static DateTime getEndOfWeek(int week, int year) {
        return new DateTime()
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
}
