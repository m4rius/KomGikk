package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.*;

import java.util.Map;

public class User {
    public static final String kind = "USER";

    private String username;
    private String password;
    private String name;
    private String email;

    private User() {

    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public User(Map map) {
        this.username = (String) map.get("username");
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.password = (String) map.get("password");
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

    public User store() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity entity = new Entity(kind, username);
        entity.setProperty("password", password);
        entity.setProperty("name", name);
        datastore.put(entity);
        return this;
    }

    public static User get(String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey(User.kind, username);

        try {
            return from(datastore.get(key));
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private static User from(Entity entity) {
        User user = new User();
        user.username = entity.getKey().getName();
        user.name = (String) entity.getProperty("name");
        user.password = (String) entity.getProperty("password");
        return user;
    }

}
