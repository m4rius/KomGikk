package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    public static final String kind = "Activity";
    public static final Activity undefined = new Activity(null, "undefined", null, null);

    private String key;
    private KomGikkUser user;
    private String name;
    private String type;
    private String sap;

    private Activity() {
    }

    public Activity(KomGikkUser user, String name, String type, String sap) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.sap = sap;
    }

    public String getName() {
        return name;
    }

    public Activity store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity(kind);
        entity.setProperty("user", user.getUsername());
        entity.setProperty("name", name);
        entity.setProperty("type", type);
        entity.setProperty("sap", sap);

        datastore.put(entity);
        key = KeyFactory.keyToString(entity.getKey());
        return this;
    }

    public static Activity from(Entity entity, KomGikkUser user) {
        Activity activity = new Activity();
        activity.user = user;
        activity.name = (String) entity.getProperty("name");
        activity.key = KeyFactory.keyToString(entity.getKey());
        activity.type = (String) entity.getProperty("type");
        activity.sap = (String) entity.getProperty("sap");
        return activity;
    }

    public static List<Activity> get(KomGikkUser user) {
        Query.Filter userFilter = new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getUsername());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(Activity.kind).setFilter(userFilter);
        PreparedQuery pq = datastore.prepare(q);

        List<Activity> result = new ArrayList<>();
        for (Entity entity : pq.asIterable()) {
            result.add(Activity.from(entity, user));
        }

        return result;

    }

    public String getKey() {
        return key;
    }

    public void setUser(KomGikkUser user) {
        this.user = user;
    }
}
