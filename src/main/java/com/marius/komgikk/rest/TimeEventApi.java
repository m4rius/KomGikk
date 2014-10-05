package com.marius.komgikk.rest;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.domain.json.JsonDate;
import com.marius.komgikk.domain.json.JsonKeyInput;
import com.marius.komgikk.domain.json.JsonTimeEvent;
import com.marius.komgikk.domain.json.JsonWorkingDay;
import com.marius.komgikk.service.UserService;
import com.marius.komgikk.util.DateUtil;
import com.marius.komgikk.util.StopClock;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/timeevent")
public class TimeEventApi {

    private static final Logger LOG = Logger.getLogger(TimeEvent.class.getName());

    private UserService userService = new UserService();

    @GET
    @Path("/list/{year}/{month}/{day}")
    @Produces(APPLICATION_JSON)
    public String list(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {
        StopClock stopClock = new StopClock().start();

        LocalDate listDate = year == 0 ? LocalDate.now() : new LocalDate(year, month, day);

        KomGikkUser currentUser = userService.getCurrentUser();
        List<JsonTimeEvent> jsonTimeEvents = TimeEvent.allForJson(currentUser, listDate);

        JsonWorkingDay workingDay = createWorkingDate(jsonTimeEvents, listDate);

        LOG.info(String.format("List time event for %s: %s millis",
                listDate.toString("dd.MM.yyyy"),
                stopClock.getElapsedTime()));

        return new Gson().toJson(workingDay);
    }

    private JsonWorkingDay createWorkingDate(List<JsonTimeEvent> jsonTimeEvents, LocalDate listDate) {
        JsonWorkingDay jsonWorkingDay = new JsonWorkingDay();
        jsonWorkingDay.events = jsonTimeEvents;

        jsonWorkingDay.prevDate = JsonDate.from(listDate.minusDays(1));
        jsonWorkingDay.selectedDate = JsonDate.from(listDate);

        if (listDate.isBefore(DateUtil.now().toLocalDate())) {
            jsonWorkingDay.nextDate = JsonDate.from(listDate.plusDays(1));
        }

        return jsonWorkingDay;
    }

    @PUT
    @Path("/list")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String updateAll(String json) {
        List<JsonTimeEvent> events = new Gson().fromJson(json, new TypeToken<List<JsonTimeEvent>>() {}.getType());

        KomGikkUser currentUser = userService.getCurrentUser();
        List<JsonTimeEvent> returnValue = new ArrayList<>();
        for (JsonTimeEvent event : checkNotNull(events)) {
            if (event.isNew) {
                DateTime dateTime = DateTime.parse(event.date + " " + event.time, DateTimeFormat.forPattern("dd.MM.yyyy HH:mm"));
                TimeEvent newEvent = new TimeEvent(currentUser, dateTime, event.activity.key).store();
                returnValue.add(newEvent.forJson());
            } else if (event.isDeleted) {
                TimeEvent.fromKey(event.key, currentUser).delete();
            } else {
                TimeEvent timeEvent = TimeEvent.fromKey(event.key, currentUser);
                timeEvent.updateFromJson(event).store();
                returnValue.add(timeEvent.forJson());
            }
        }

        return new Gson().toJson(returnValue);


    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String storeNew(String json) {
        JsonKeyInput jsonKeyInput = new Gson().fromJson(json, JsonKeyInput.class);

        checkNotNull(jsonKeyInput.key);

        KomGikkUser currentUser = userService.getCurrentUser();

        TimeEvent timeEvent = new TimeEvent(currentUser, DateUtil.now(), jsonKeyInput.key);
        timeEvent.store();
        LOG.info(String.format("Stored new time event with action key: %s", jsonKeyInput.key ));

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
