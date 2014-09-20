package com.marius.komgikk.rest;

import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.JsonActivity;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/activity")
public class ActivityApi {

    private UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void storeActivity(String json) {
        JsonActivity jsonActivity = new Gson().fromJson(json, JsonActivity.class);
        if ("delete".equals(jsonActivity.action)) {
            deleteActivity(json);
        } else {
            Activity activity = new Activity(userService.getCurrentUser(), jsonActivity.name, jsonActivity.sap);
            activity.store();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{key}")
    public String getActivity(@PathParam("key") String key) {
        JsonActivity activity = Activity.getByKey(key, userService.getCurrentUser()).forJson();
        return new Gson().toJson(activity);
    }


    //@DELETE
    //TODO: Får ikke til å kalle denne fra GUI
    public void deleteActivity(String json) {
        JsonActivity jsonActivity = new Gson().fromJson(json, JsonActivity.class);

        Activity activity = Activity.findStored(jsonActivity, userService.getCurrentUser());
        activity.delete();

    }
}
