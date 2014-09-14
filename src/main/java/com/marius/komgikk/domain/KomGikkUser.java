package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;

import java.util.Map;

public class KomGikkUser {
    public static final String kind = "USER";

    private String username;
    private String password;
    private String name;
    private String email;

    private KomGikkUser() {

    }

    public KomGikkUser(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public KomGikkUser(Map map) {
        this.username = (String) map.get("username");
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.password = (String) map.get("password");
    }

    public KomGikkUser(String userId, String email, String nickname) {
        this.username = userId;
        this.email = email;
        //todo legg inn nickname
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public KomGikkUser store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity entity = new Entity(kind, username);
        entity.setProperty("password", password);
        entity.setProperty("name", name);
        entity.setProperty("email", email);
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
        KomGikkUser user = new KomGikkUser();
        user.username = entity.getKey().getName();
        user.name = (String) entity.getProperty("name");
        user.password = (String) entity.getProperty("password");
        user.email = (String) entity.getProperty("email");
        return user;
    }

}
