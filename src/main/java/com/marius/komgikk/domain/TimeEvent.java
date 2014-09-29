package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.marius.komgikk.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

public class TimeEvent extends DataStoreDependentDomain {

    public static final String kind = "TIME_EVENT";

    private Entity entity;

    //bare for å slippe å hente dem flere ganger.
    private transient Activity activity;
    private transient KomGikkUser user;

    /*
    Constructors
     */

    private TimeEvent(Entity entity) {
        this.entity = entity;
    }

    public TimeEvent(KomGikkUser user, DateTime time, String activity) {
        this.entity = new Entity(kind, user.getKey());
        setDateTime(time);
        setActivityKey(activity);
        setUser(user);
    }

    public TimeEvent(KomGikkUser user, DateTime time, Activity activity) {
        this(user, time, activity.getKeyString());
        this.activity = activity;
    }

    /*
    Getters and setters
     */

    public void setDateTime(DateTime time) {
        entity.setProperty("dateTime", time.toDate());
    }

    public void setDateTime(String time) {
        setDateTime(DateTime.parse(time, DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")));
    }

    private void setActivityKey(String activity) {
        entity.setProperty("activityKey", activity);
    }


    private String getActivityKey() {
        return (String) entity.getProperty("activityKey");
    }

    private void setUser(KomGikkUser user) {
        this.user = user;
    }

    private KomGikkUser getUser() {
        if (user == null) {
            user = new UserService().getCurrentUser();
        }
        return user;
    }

    private Activity getActivity() {
        if (activity == null) {
            activity = Activity.getByKey(getActivityKey(), getUser());
        }
        return activity;
    }

    /*
     * Datastore
     */

    public TimeEvent store() {
        getDataStore().put(entity);
        return this;
    }

    public void delete() {
        getDataStore().delete(entity.getKey());
    }


    public static List<TimeEvent> all(KomGikkUser user, DateTime dateTime) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter minTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.GREATER_THAN_OR_EQUAL, dateTime.withTimeAtStartOfDay().toDate());
        Query.Filter maxTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.LESS_THAN_OR_EQUAL, dateTime.withTimeAtStartOfDay().plusDays(1).toDate());


        Query q = new Query(kind)
                .setAncestor(user.getKey())
                .setFilter(Query.CompositeFilterOperator.and(minTimeFilter, maxTimeFilter))
                .addSort("dateTime");

        PreparedQuery preparedQuery = datastore.prepare(q);

        List<TimeEvent> result = new ArrayList<>();
        for (Entity entity : preparedQuery.asIterable()) {
            result.add(new TimeEvent(entity));
        }

        return result;
    }


    public static TimeEvent fromKey(String key, KomGikkUser currentUser) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        try {
            Entity entity = datastore.get(KeyFactory.stringToKey(key));
            if (entity.getParent().equals(currentUser.getKey())) {
                return new TimeEvent(entity);
            }
        } catch (EntityNotFoundException e) {
            //TODO returner noe mer fornuftig
            e.printStackTrace();
        }
        return null;
    }


    /*
     * Json
     */

    public JsonTimeEvent forJson() {
        JsonTimeEvent jsonTimeEvent = new JsonTimeEvent();
        jsonTimeEvent.key = KeyFactory.keyToString(entity.getKey());

        DateTime dateTime = new DateTime(entity.getProperty("dateTime"), DateTimeZone.forID("Europe/Oslo"));

        jsonTimeEvent.time = dateTime.toString("HH:mm");
        jsonTimeEvent.date = dateTime.toString("dd.MM.yyyy");

        jsonTimeEvent.activity = getActivity().forJson();

        return jsonTimeEvent;
    }

    public static List<JsonTimeEvent> allForJson(KomGikkUser user, DateTime dateTime) {
        List<TimeEvent> timeEvents = all(user, dateTime);

        return Lists.transform(timeEvents, new Function<TimeEvent, JsonTimeEvent>() {
            public JsonTimeEvent apply(TimeEvent i) {
                return i.forJson();
            }
        });
    }

    public TimeEvent updateFromJson(JsonTimeEvent json) {
        setActivityKey(json.activity.key);
        setDateTime(json.date + " " + json.time);
        return this;
    }
}
