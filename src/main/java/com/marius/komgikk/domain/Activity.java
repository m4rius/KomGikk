package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    public static final String kind = "ACTIVITY";

    private Entity entity;

    /*
    Constructors
     */

    private Activity(KomGikkUser user) {
        this.entity = new Entity(kind, user.getKey());
    }

    public Activity(KomGikkUser user, String name, String sap, String category) {
        this(user);
        entity.setProperty("user", user.getUsername());
        setName(name);
        setSap(sap);
        setCategory(category);
        setState(ActivityState.CURRENT);

    }

    private static Activity from(Entity entity, KomGikkUser user) {
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

    public String getKeyString() {
        return KeyFactory.keyToString(entity.getKey());
    }

    /*
    datastore
     */

    public Activity store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);
        return this;
    }

    public void delete() {
        setState(ActivityState.HISTORIC);
        store();
    }

    public static List<JsonActivity> getForJson(KomGikkUser user) {
        Query.Filter stateFilter = new Query.FilterPredicate("state", Query.FilterOperator.EQUAL, ActivityState.CURRENT.name());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(Activity.kind)
                .setAncestor(user.getKey())
                .setFilter(stateFilter);

        PreparedQuery pq = datastore.prepare(q);

        List<JsonActivity> result = new ArrayList<>();
        for (Entity entity : pq.asIterable()) {
            result.add(Activity.from(entity, user).forJson());
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
        activity.name = getName();
        activity.category = getCategory();
        activity.sap = getSap();
        return activity;
    }


    public enum ActivityState {
        CURRENT, HISTORIC
    }
}
