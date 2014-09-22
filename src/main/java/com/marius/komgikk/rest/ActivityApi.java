package com.marius.komgikk.rest;

import com.google.common.base.Preconditions;
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
    @Produces(MediaType.APPLICATION_JSON)
    public String newActivity(String json) {
        JsonActivity jsonActivity = new Gson().fromJson(json, JsonActivity.class);

        //todo burde vært i @DELETE
        if ("delete".equals(jsonActivity.action)) {
            return deleteActivity(json);
        } else {
            Activity activity = new Activity(userService.getCurrentUser(), jsonActivity.name, jsonActivity.sap, jsonActivity.category);
            activity.store();
            return new Gson().toJson(activity.forJson());
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateActivity(String json) {
        JsonActivity jsonActivity = new Gson().fromJson(json, JsonActivity.class);

        if (jsonActivity.key == null) {
            return newActivity(json);
        } else {
            Activity activity = Activity.update(jsonActivity, userService.getCurrentUser());
            return new Gson().toJson(activity.forJson());
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
    public String deleteActivity(String json) {
        JsonActivity jsonActivity = new Gson().fromJson(json, JsonActivity.class);

        Preconditions.checkNotNull(jsonActivity.key, "Missing key");

        Activity activity = Activity.findStored(jsonActivity, userService.getCurrentUser());
        activity.delete();

        return new Gson().toJson(activity.forJson());
    }
}
