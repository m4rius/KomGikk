package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    public static final String kind = "ACTIVITY";

    private Entity entity;

    private Activity(KomGikkUser user) {
        this.entity = new Entity(kind, user.getEntity().getKey());
    }

    public Activity(KomGikkUser user, String name, String sap) {
        this(user);
        entity.setProperty("user", user.getUsername());
        entity.setProperty("name", name);
        entity.setProperty("sap", sap);
        entity.setProperty("state", ActivityState.CURRENT.name());
    }

    public String getName() {
        return (String) entity.getProperty("name");
    }

    public String getSap() {
        return (String) entity.getProperty("sap");
    }

    public String getKey() {
        return KeyFactory.keyToString(entity.getKey());
    }

    public Activity store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);
        return this;
    }

    public void delete() {
        entity.setProperty("state", ActivityState.HISTORIC.name());
        store();
    }

    public static Activity from(Entity entity, KomGikkUser user) {
        Activity activity = new Activity(user);
        activity.entity = entity;
        return activity;
    }

    public static List<JsonActivity> getForJson(KomGikkUser user) {
        Query.Filter stateFilter = new Query.FilterPredicate("state", Query.FilterOperator.EQUAL, ActivityState.CURRENT.name());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(Activity.kind)
                .setAncestor(user.getEntity().getKey())
                .setFilter(stateFilter);

        PreparedQuery pq = datastore.prepare(q);

        List<JsonActivity> result = new ArrayList<>();
        for (Entity entity : pq.asIterable()) {
            result.add(JsonActivity.from(entity));
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


    private enum ActivityState {
        CURRENT, HISTORIC
    }
}
