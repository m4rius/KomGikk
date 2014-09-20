package com.marius.komgikk.rest;

import com.google.gson.Gson;
import com.marius.komgikk.domain.JsonTimeEvent;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.service.UserService;
import org.joda.time.DateTime;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/timeevent")
public class TimeEventApi {

    private UserService userService = new UserService();

    @GET
    @Path("/all")
    @Produces(APPLICATION_JSON)
    public String list() {
        KomGikkUser currentUser = userService.getCurrentUser();
        return null;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String storeNew(String json) {
        JsonTimeEvent jsonTimeEvent = new Gson().fromJson(json, JsonTimeEvent.class);
        KomGikkUser currentUser = userService.getCurrentUser();

        TimeEvent timeEvent;
        if (jsonTimeEvent.specialEvent != null) {
            timeEvent = new TimeEvent(
                    currentUser,
                    DateTime.now(),
                    TimeEvent.TimeEventSpecialType.valueOf(jsonTimeEvent.specialEvent));
        } else {
            timeEvent = new TimeEvent(currentUser, DateTime.now(), jsonTimeEvent.activityKey);
        }

        timeEvent.store();


        return new Gson().toJson(timeEvent.forJson());
    }

}
