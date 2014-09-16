package com.marius.komgikk.rest;


import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/activities")
public class ActivitiesApi {

    private UserService userService = new UserService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //TODO: Returnere activitet kun med de felt som brukes i web (ikke bruker info f.eks)
    public String getAllActivities() {
        List<Activity> all = Activity.get(userService.getCurrentUser());
        return new Gson().toJson(all);
    }
}
