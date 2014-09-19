package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class JsonActivity {
    public String key;
    public String name;
    public String sap;
    public String action;

    public static JsonActivity from(Entity entity) {
        JsonActivity activity = new JsonActivity();
        activity.key = KeyFactory.keyToString(entity.getKey());
        activity.name = (String) entity.getProperty("name");
        activity.sap = (String) entity.getProperty("sap");
        return activity;
    }
}
