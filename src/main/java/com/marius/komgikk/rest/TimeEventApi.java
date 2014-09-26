package com.marius.komgikk.rest;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.marius.komgikk.domain.JsonTimeEvent;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/timeevent")
public class TimeEventApi {

    private UserService userService = new UserService();

    @GET
    @Path("/list")
    @Produces(APPLICATION_JSON)
    public String list() {
        KomGikkUser currentUser = userService.getCurrentUser();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Oslo");
        List<JsonTimeEvent> jsonTimeEvents = TimeEvent.allForJson(currentUser, DateTime.now(dateTimeZone));
        return new Gson().toJson(jsonTimeEvents);
    }

    @PUT
    @Path("/list")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String updateAll(String json) {
        List<JsonTimeEvent> events = new Gson().fromJson(json, new TypeToken<List<JsonTimeEvent>>() {}.getType());

        KomGikkUser currentUser = userService.getCurrentUser();
        List<JsonTimeEvent> returnValue = new ArrayList<>();
        for (JsonTimeEvent event : events) {
            if (event.isNew) {
                DateTime dateTime = DateTime.parse(event.date + " " + event.time, DateTimeFormat.forPattern("dd.MM.yyyy HH:mm"));
                TimeEvent newEvent = new TimeEvent(currentUser, dateTime, event.activityKey).store();
                returnValue.add(newEvent.forJson());
            } else if (event.isDeleted) {
                TimeEvent.fromKey(event.key, currentUser).delete();
            } else {
                TimeEvent timeEvent = TimeEvent.fromKey(event.key, currentUser);
                timeEvent.update(event).store();
                returnValue.add(timeEvent.forJson());
            }
        }

        return new Gson().toJson(returnValue);


    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String storeNew(String json) {
        JsonTimeEvent jsonTimeEvent = new Gson().fromJson(json, JsonTimeEvent.class);
        KomGikkUser currentUser = userService.getCurrentUser();

        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Oslo");

        TimeEvent timeEvent;
        if (jsonTimeEvent.specialEvent != null) {
            timeEvent = new TimeEvent(
                    currentUser,
                    DateTime.now(dateTimeZone),
                    TimeEvent.TimeEventSpecialType.valueOf(jsonTimeEvent.specialEvent));
        } else {
            timeEvent = new TimeEvent(currentUser, DateTime.now(dateTimeZone), jsonTimeEvent.activityKey);
        }

        timeEvent.store();


        return new Gson().toJson(timeEvent.forJson());
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String update(String json) {
        JsonTimeEvent jsonTimeEvent = new Gson().fromJson(json, JsonTimeEvent.class);

        KomGikkUser currentUser = userService.getCurrentUser();

        TimeEvent timeEvent = TimeEvent.fromKey(jsonTimeEvent.key, currentUser);

        //TODO validate date and time format

        timeEvent.setDateTime(jsonTimeEvent.date + " " +jsonTimeEvent.time);

        return new Gson().toJson(timeEvent.forJson());
    }


}
