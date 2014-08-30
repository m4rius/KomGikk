package com.marius.komgikk.user;

import com.google.appengine.api.datastore.Entity;

public class User {
    public static final String kind = "USER";

    private String username;
    private String password;
    private String name;

    private User() {

    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
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

    public Entity createEntity() {
        Entity entity = new Entity(kind, username);
        entity.setProperty("password", password);
        entity.setProperty("name", name);
        return entity;
    }

    public static User from(Entity entity) {
        User user = new User();
        user.username = entity.getKey().getName();
        user.name = (String) entity.getProperty("name");
        user.password = (String) entity.getProperty("password");
        return user;
    }
}
