package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Preconditions;
import com.marius.komgikk.domain.json.JsonKomGikkUser;

public class KomGikkUser {
    public static final String kind = "USER";

    private Entity entity;

    public transient boolean createdNow = false;

    private boolean admin = false;

    public KomGikkUser(String username) {
        entity = new Entity(kind, username);
    }

    public KomGikkUser(String username, String email) {
        this(username, null, email);
    }

    public KomGikkUser(String username, String name, String email) {
        this(username);
        this.entity.setProperty("name", name);
        this.entity.setProperty("email", email);

        checkProperties();
    }

    public String getUsername() {
        return entity.getKey().getName();
    }

    public String getName() {
        return (String) entity.getProperty("name");
    }

    public String getEmail() {
        return (String) entity.getProperty("email");
    }

    public Key getKey() {
        return entity.getKey();
    }

    public void setName(String name) {
        entity.setProperty("name", name);
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public KomGikkUser store() {
        checkProperties();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);

        return this;
    }

    public static KomGikkUser get(String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey(KomGikkUser.kind, username);

        try {
            return from(datastore.get(key));
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private static KomGikkUser from(Entity entity) {

        KomGikkUser user = new KomGikkUser(entity.getKey().getName());
        user.entity = entity;

        user.checkProperties();
        return user;
    }

    private void checkProperties() {
        Preconditions.checkNotNull(entity.getProperty("email"));
    }

    public JsonKomGikkUser forJson() {
        JsonKomGikkUser jsonKomGikkUser = new JsonKomGikkUser();
        jsonKomGikkUser.username = getUsername();
        jsonKomGikkUser.email = getEmail();
        jsonKomGikkUser.name = getName();
        jsonKomGikkUser.admin = admin;

        return jsonKomGikkUser;
    }
}
