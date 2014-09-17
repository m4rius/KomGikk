package com.marius.komgikk.rest;


import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/properties")
public class PropertiesApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSystemProperties() {
        Map<String, String> properties = new HashMap<>();

        UserService userService = UserServiceFactory.getUserService();
        properties.put("logoutUrl", userService.createLogoutURL("/index.html"));

        return new Gson().toJson(properties);
    }
}
