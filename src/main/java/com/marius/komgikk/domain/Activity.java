package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.marius.komgikk.domain.json.JsonActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Activity extends  DataStoreDependentDomain {

    private static final Logger LOG = Logger.getLogger(Activity.class.getName());

    public static final String kind = "ACTIVITY";

    private Entity entity;


    /*
    Constructors
     */

    private Activity(KomGikkUser user) {
        this.entity = new Entity(kind, user.getKey());
        setState(ActivityState.CURRENT);
        entity.setProperty("user", user.getUsername());
    }

    public Activity(KomGikkUser user, String name, String sap, String category) {
        this(user);
        setName(name);
        setSap(sap);
        setCategory(category);

    }

    private static Activity defaultActivity(KomGikkUser user, DefaultActivities type) {
        Activity activity = new Activity(user);
        setDefaultType(type, activity);
        return activity;
    }

    public static Activity from(Entity entity, KomGikkUser user) {
        Activity activity = new Activity(user);
        activity.entity = entity;
        return activity;
    }

    /*
    getters/setters
     */

    public String getName() {
        return (String) entity.getProperty("name");
    }

    private void setName(String name) {
        entity.setProperty("name", name);
    }

    public String getSap() {
        return (String) entity.getProperty("sap");
    }

    private void setSap(String sap) {
        entity.setProperty("sap", sap);
    }

    public String getCategory() {
        return (String) entity.getProperty("category");
    }

    private void setCategory(String category) {
        entity.setProperty("category", category);
    }

    private void setState(ActivityState activityState) {
        entity.setProperty("state", activityState.name());
    }

    public ActivityState getState() {
        return ActivityState.valueOf((String) entity.getProperty("state"));
    }

    public String getKeyString() {
        return KeyFactory.keyToString(entity.getKey());
    }

    private static void setDefaultType(DefaultActivities type, Activity activity) {
        activity.entity.setProperty("defaultType", type.name());
    }

    public DefaultActivities getDefaultType() {
        String defaultType = (String) entity.getProperty("defaultType");
        return defaultType != null ? DefaultActivities.valueOf(defaultType) : null;
    }

    /*
    datastore
     */

    public Activity store() {
        getDataStore().put(entity);
        return this;
    }

    public void delete() {
        setState(ActivityState.HISTORIC);
        store();
    }

    public static List<Activity> allCurrent(KomGikkUser user) {
        Query.Filter stateFilter = new Query.FilterPredicate("state", Query.FilterOperator.EQUAL, ActivityState.CURRENT.name());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(Activity.kind)
                .setAncestor(user.getKey())
                .setFilter(stateFilter);

        PreparedQuery pq = datastore.prepare(q);

        List<Activity> result = new ArrayList<>();
        for (Entity entity : pq.asIterable()) {
            result.add(Activity.from(entity, user));
        }

        return result;

    }

    public static Map<String, Activity> getAllActivitiesByKey(KomGikkUser user) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(Activity.kind).setAncestor(user.getKey());
        Map<String, Activity> result = new HashMap<>();
        for (Entity entity : datastore.prepare(q).asIterable()) {
            Activity a = Activity.from(entity, user);
            result.put(a.getKeyString(), a);
        }

        return result;
    }

    public static Activity findStored(JsonActivity jsonActivity, KomGikkUser user) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity entity = datastore.get(KeyFactory.stringToKey(jsonActivity.key));
            return Activity.from(entity, user);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }


    public static Activity getByKey(String key, KomGikkUser user) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity entity = datastore.get(KeyFactory.stringToKey(key));
            return Activity.from(entity, user);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public static void storeDefaults(KomGikkUser user) {
        LOG.info(String.format("Storing default actions for user %s", user.getUsername()));

        Activity.defaultActivity(user, DefaultActivities.START).store();
        Activity.defaultActivity(user, DefaultActivities.END).store();

        Activity.defaultActivity(user, DefaultActivities.START_EXTRA).store();
        Activity.defaultActivity(user, DefaultActivities.END_EXTRA).store();

    }

    public static Activity update(JsonActivity jsonActivity, KomGikkUser currentUser) {
        Preconditions.checkNotNull(jsonActivity.key, "Can not update activity without key");

        Activity stored = Activity.findStored(jsonActivity, currentUser);
        stored.setName(jsonActivity.name);
        stored.setSap(jsonActivity.sap);
        stored.setCategory(jsonActivity.category);
        stored.store();
        return stored;
    }

     /*
    Json
     */

    public JsonActivity forJson() {
        JsonActivity activity = new JsonActivity();
        activity.key = KeyFactory.keyToString(entity.getKey());
        activity.defaultType = getDefaultType() != null ? getDefaultType().name() : null;
        if (activity.defaultType == null) {
            activity.name = getName();
            activity.category = getCategory();
            activity.sap = getSap();
        }

        return activity;
    }

    public static List<JsonActivity> getForJson(KomGikkUser user) {
        List<Activity> timeEvents = allCurrent(user);

        return Lists.transform(timeEvents, new Function<Activity, JsonActivity>() {
            public JsonActivity apply(Activity i) {
                return i.forJson();
            }
        });
    }
    public enum ActivityState {
        CURRENT, HISTORIC;
    }

    public enum DefaultActivities {
        START, END, START_EXTRA, END_EXTRA
    }

    @Override
    public String toString() {
        return String.format("Activity: %s", getName());
    }
}
