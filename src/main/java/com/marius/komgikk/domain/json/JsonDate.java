package com.marius.komgikk.domain.json;

import org.joda.time.LocalDate;

public class JsonDate {
    public int year;
    public int month;
    public int day;

    public static JsonDate from(LocalDate localDate) {
        JsonDate date = new JsonDate();
        date.year = localDate.getYear();
        date.month = localDate.getMonthOfYear();
        date.day = localDate.getDayOfMonth();

        return date;
    }
}
