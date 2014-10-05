package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.marius.komgikk.domain.json.JsonTimeEvent;
import com.marius.komgikk.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeEvent extends DataStoreDependentDomain implements Comparable<TimeEvent> {

    private static final Logger log = Logger.getLogger(TimeEvent.class.getName());

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

    public static TimeEvent from(Entity entity, KomGikkUser user) {
        TimeEvent timeEvent = new TimeEvent(entity);
        timeEvent.user = user;
        return timeEvent;
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

    public DateTime getDateTime() {
        return new DateTime(entity.getProperty("dateTime"), DateTimeZone.forID("Europe/Oslo"));
    }



    private void setActivityKey(String activity) {
        entity.setProperty("activityKey", activity);
    }

    public String getActivityKey() {
        return (String) entity.getProperty("activityKey");
    }

    public Activity getActivity() {
        if (activity == null) {
            activity = Activity.getByKey(getActivityKey(), getUser());
            if (activity == null) {
                log.log(Level.SEVERE, String.format("Activity with key %s conected to time event %s not found", getActivityKey(), entity.getKey()));
            }
        }
        return activity;
    }

    public void setActivity(Map<String, Activity> activityMap) {
        if (activityMap.containsKey(getActivityKey())) {
            activity = activityMap.get(getActivityKey());
        } else {
            log.warning(String.format("Could not find activity in provided map. Trying to get from datastore"));
            getActivity();
        }
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


    public static List<TimeEvent> all(KomGikkUser user, LocalDate date) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter minTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.GREATER_THAN_OR_EQUAL, date.toDate());
        Query.Filter maxTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.LESS_THAN_OR_EQUAL, date.plusDays(1).toDate());


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

    public static Map<LocalDate, List<TimeEvent>> allBetween(KomGikkUser user, DateTime startDate, DateTime endDate) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter minTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.GREATER_THAN_OR_EQUAL, startDate.toDate());
        Query.Filter maxTimeFilter = new Query.FilterPredicate("dateTime", Query.FilterOperator.LESS_THAN_OR_EQUAL, endDate.toDate());

        Query q = new Query(TimeEvent.kind)
                .setAncestor(user.getKey())
                .setFilter(Query.CompositeFilterOperator.and(minTimeFilter, maxTimeFilter));

        Map<LocalDate, List<TimeEvent>> result = new TreeMap<>();

        for (Entity entity : datastore.prepare(q).asIterable()) {
            TimeEvent te = TimeEvent.from(entity, user);
            LocalDate date = te.getDateTime().toLocalDate();

            if (!result.containsKey(date)) {
                result.put(date, new ArrayList<TimeEvent>());
            }

            result.get(date).add(te);
        }

        return result;
    }


    /*
     * Json
     */

    public JsonTimeEvent forJson() {
        JsonTimeEvent jsonTimeEvent = new JsonTimeEvent();
        jsonTimeEvent.key = KeyFactory.keyToString(entity.getKey());

        DateTime dateTime = getDateTime();

        jsonTimeEvent.time = dateTime.toString("HH:mm");
        jsonTimeEvent.date = dateTime.toString("dd.MM.yyyy");

        jsonTimeEvent.activity = getActivity().forJson();

        return jsonTimeEvent;
    }

    public static List<JsonTimeEvent> allForJson(KomGikkUser user, LocalDate date) {
        List<TimeEvent> timeEvents = all(user, date);

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

    @Override
    public int compareTo(TimeEvent o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }

    @Override
    public String toString() {
        return String.format("Activity: %s, Occurred at %s",
                activity != null ? activity.getName() : getActivityKey(),
                getDateTime().toString("HH:mm"));
    }
}
