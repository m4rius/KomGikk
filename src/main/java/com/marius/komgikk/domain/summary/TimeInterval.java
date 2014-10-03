package com.marius.komgikk.domain.summary;

import org.joda.time.DateTime;

public class TimeInterval {

    public DateTime fra;
    public DateTime til;
    public int minutes;

    public TimeInterval(DateTime fra, DateTime til) {
        this.fra = fra;
        this.til = til;
        if (til != null) {
            this.minutes = til.getMinuteOfDay() - fra.getMinuteOfDay();
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %s min", fra.toString("HH:mm"), til.toString("HH:mm"), minutes);
    }
}
