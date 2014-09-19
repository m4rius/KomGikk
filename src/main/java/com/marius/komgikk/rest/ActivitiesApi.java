package com.marius.komgikk.rest;


import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/activities")
public class ActivitiesApi {

    private UserService userService = new UserService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllActivities() {
        return new Gson().toJson(Activity.getForJson(userService.getCurrentUser()));
    }
}
