package com.marius.komgikk.domain;

public class JsonTimeEvent {
    public String key;
    public String time;
    public String date;
    public boolean isNew;
    public boolean isDeleted;

    public JsonActivity activity;

    public String toString() {
        return String.format("JsonTimeEvent: key: %s, time: %s, date: %s, activity, %s/%s, isNew: %b, isDeleted; %b"
        , key, time, date, activity.name, activity.defaultType , isNew, isDeleted);
    }
}

