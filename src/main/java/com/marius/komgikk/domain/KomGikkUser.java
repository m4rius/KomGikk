package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Preconditions;

public class KomGikkUser {
    public static final String kind = "USER";

    private Entity entity;

    public KomGikkUser(String username) {
        entity = new Entity(kind, username);
        entity.setProperty("username", username);
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
        return (String) entity.getProperty("username");
    }

    public String getName() {
        return (String) entity.getProperty("name");
    }

    public String getEmail() {
        return (String) entity.getProperty("email");
    }

    //TODO bare gi ut key
    @Deprecated
    public Entity getEntity() {
        return entity;
    }

    public Key getKey() {
        return entity.getKey();
    }

    public void setName(String name) {
        entity.setProperty("name", name);
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
        Preconditions.checkNotNull(entity.getProperty("username"));
        Preconditions.checkNotNull(entity.getProperty("email"));
    }

    public JsonKomGikkUser forJson() {
        JsonKomGikkUser jsonKomGikkUser = new JsonKomGikkUser();
        jsonKomGikkUser.username = getUsername();
        jsonKomGikkUser.email = getEmail();
        jsonKomGikkUser.name = getName();

        return jsonKomGikkUser;
    }
}
