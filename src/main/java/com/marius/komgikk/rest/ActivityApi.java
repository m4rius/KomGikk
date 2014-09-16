package com.marius.komgikk.rest;

import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/activity")
public class ActivityApi {

    private UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void storeActivity(String json) {
        Activity activity = new Gson().fromJson(json, Activity.class);
        activity.setUser(userService.getCurrentUser());

        activity.store();
    }
}
