package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeEvent {

    public static final String kind = "TimeEvent";

    private KomGikkUser user;
    private DateTime started;
    private Activity activity;
    private String key;

    private TimeEvent() {
    }

    public TimeEvent(KomGikkUser user, DateTime started, Activity activity) {
        this.user = user;
        this.started = started;
        this.activity = activity;
    }

    public KomGikkUser getUser() {
        return user;
    }

    public DateTime getStarted() {
        return started;
    }

    public Activity getActivity() {
        return activity;
    }

    public TimeEvent store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity(kind);
        entity.setProperty("user", user.getUsername());
        entity.setProperty("started", started.toDate());
        entity.setProperty("activity", activity.getKey());
        datastore.put(entity);

        return this;
    }

    public static List<TimeEvent> get(KomGikkUser user) {
        Query.Filter userFilter = new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getUsername());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(kind).setFilter(userFilter);

        PreparedQuery pq = datastore.prepare(q);

        List<TimeEvent> result = new ArrayList<>();
        for (Entity entity : pq.asIterable()) {
            result.add(TimeEvent.from(entity, user));
        }

        return result;
    }

    private static TimeEvent from(Entity entity, KomGikkUser user) {
        TimeEvent te = new TimeEvent();
        te.key = KeyFactory.keyToString(entity.getKey());
        te.user = user;
        te.started = new DateTime(((Date) entity.getProperty("started")).getTime());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity entityActivity = datastore.get(KeyFactory.stringToKey((String) entity.getProperty("activity")));
            te.activity = Activity.from(entityActivity, user);
        } catch (EntityNotFoundException e) {
        }

        return te;
    }
}
