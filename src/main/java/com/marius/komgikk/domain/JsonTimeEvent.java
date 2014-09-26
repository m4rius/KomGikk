package com.marius.komgikk.domain;

public class JsonTimeEvent {
    public String key;
    public String time;
    public String date;
    public String activityKey;
    public String specialEvent;
    public boolean isNew;
    public boolean isDeleted;

    public String toString() {
        return String.format("JsonTimeEvent: key: %s, time: %s, date: %s, specialEvent:, %s, activity, %s, isNew: %b, isDeleted; %b"
        , key, time, date, specialEvent, activityKey, isNew, isDeleted);
    }
}

