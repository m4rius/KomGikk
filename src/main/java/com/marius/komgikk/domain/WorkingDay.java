package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.Entity;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public class WorkingDay {

    public static final String kind = "WorkingDay";

    private KomGikkUser user;
    private Date date;
    private DateTime started;
    private DateTime ended;

    private List<TimeEvent> events;

    public void store() {
        //Todo dersom det finnes skal det oppdateres

        new Entity(kind);



    }
}
