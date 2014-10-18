package com.marius.komgikk.rest;


import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.domain.json.JsonTimeSummary;
import com.marius.komgikk.domain.json.JsonTimeSummaryDay;
import com.marius.komgikk.domain.summary.TimeInterval;
import com.marius.komgikk.domain.summary.WorkTimeAccumulator;
import com.marius.komgikk.service.UserService;
import com.marius.komgikk.util.DateUtil;
import com.marius.komgikk.util.StopClock;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.logging.Logger;

import static com.marius.komgikk.domain.Activity.DefaultActivities.*;

@Path("/summary")
public class TimeSummaryApi {

    private static Logger log = Logger.getLogger(TimeSummaryApi.class.getName());

    private UserService userService = new UserService();

    private static Map<Integer, String> daysNames = new HashMap<>();

    static {
        daysNames.put(1, "Mandag");
        daysNames.put(2, "Tirsdag");
        daysNames.put(3, "Onsdag");
        daysNames.put(4, "Torsdag");
        daysNames.put(5, "Fredag");
        daysNames.put(6, "Lørdag");
        daysNames.put(7, "Søndag");
    }

    @GET
    @Path("{year}/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public String summaryForWeek(@PathParam("year")int year, @PathParam("week") int week) {
        StopClock stopClock = new StopClock().start();

        if (year == 0 || week == 0) {
            DateTime now = DateUtil.now();
            year = now.getYear();
            week = now.getWeekOfWeekyear();
        }
        log.info(String.format("Start summary for week %s", week));

        DateTime startDate = DateUtil.getStartOfWeek(week, year);
        DateTime endDate = DateUtil.getEndOfWeek(week, year);

        KomGikkUser currentUser = userService.getCurrentUser();

        Map<String, Activity> activitiesByKey = Activity.getAllActivitiesByKey(currentUser);
        Map<LocalDate, List<TimeEvent>> timeEvents = TimeEvent.allBetween(currentUser, startDate, endDate);

        JsonTimeSummary timeSummary = mashUp(timeEvents, activitiesByKey, startDate);
        timeSummary.todayWeek = DateUtil.now().getWeekOfWeekyear();

        stopClock.stop();
        log.info(String.format("Done creating summary for week %s. Time elapsed %d millis", week, stopClock.getElapsedTime()));

        return new Gson().toJson(timeSummary);

    }

    private JsonTimeSummary mashUp(Map<LocalDate, List<TimeEvent>> timeEvents, Map<String, Activity> activitiesByKey, DateTime startDate) {
        LocalDate start = startDate.toLocalDate();

        JsonTimeSummary timeSummary = new JsonTimeSummary();
        timeSummary.week = start.getWeekOfWeekyear();
        timeSummary.year = start.getYear();

        if (timeEvents.isEmpty()) {
            return timeSummary;
        }

        timeSummary.days = new ArrayList<>();

        for (LocalDate localDate : timeEvents.keySet()) {
            JsonTimeSummaryDay day = new JsonTimeSummaryDay();
            timeSummary.days.add(day);
            day.day = daysNames.get(localDate.getDayOfWeek()) + " " + localDate.toString("dd.MM");

            List<TimeEvent> timeEventsForDate = timeEvents.get(localDate);

            List<WorkTimeAccumulator> accumulators = createAccumulators(timeEventsForDate, activitiesByKey);

            if (accumulators.size() > 1) {
                WorkTimeAccumulator first = accumulators.get(0);
                WorkTimeAccumulator last = accumulators.get(accumulators.size() - 1);

                day.from = first.normalizedFrom.toString("HH:mm");
                day.to = last.normalizedTo != null ? last.normalizedTo.toString("HH:mm") : null;
            }

            double hours = 0;
            for (WorkTimeAccumulator accumulator : accumulators) {
                hours +=accumulator.getHours();
                day.activities.add(accumulator.forJson());
            }

            day.hours = Double.toString(hours);
        }

        return timeSummary;
    }

    private List<WorkTimeAccumulator> createAccumulators(List<TimeEvent> timeEventsForDate, Map<String, Activity> activitiesByKey) {

        //prevent timeevent to get their activity direct from datastore
        addActivities(timeEventsForDate, activitiesByKey);

        //Sorteres på tid
        Collections.sort(timeEventsForDate);


        if (timeEventsForDate.size() < 3) {
            return new ArrayList<>();
        }

        //TODO: Får feil dersom start og første event er samtidig. Kan skje dersom bruker har vært inne og endret tid.
        List<TimeEvent> mainBlock = filterEventBlock(timeEventsForDate, START, END);
        List<WorkTimeAccumulator> allAccumulators = accumulateBlock(mainBlock, START, END);

        //kveldsøkter
        while (timeEventsForDate.size() > 2) {
            List<TimeEvent> extraBlock = filterEventBlock(timeEventsForDate, START_EXTRA, END_EXTRA);
            if (extraBlock.size() == 0) {
                break;
            }
            allAccumulators.addAll(accumulateBlock(extraBlock, START_EXTRA, END_EXTRA));
        }

        return allAccumulators;
    }

    private List<WorkTimeAccumulator> accumulateBlock(List<TimeEvent> timeEventsForDate, Activity.DefaultActivities startType, Activity.DefaultActivities endType) {

        //Finner kom og gikk og fjerner dem fra listen
        TimeEvent startEvent = findDefault(startType, timeEventsForDate);
        TimeEvent endEvent = findDefault(endType, timeEventsForDate);

        Iterator<TimeEvent> iterator = timeEventsForDate.iterator();

        //Dagens første event
        TimeEvent event = iterator.next();

        //minutter fra Kom til første event startet. Skal deles ut på alle akumulators til slutt
        int komTilAct = event.getDateTime().getMinuteOfDay() - startEvent.getDateTime().getMinuteOfDay();

        List<WorkTimeAccumulator> allAccumulators = new ArrayList<>();

        while (iterator.hasNext()) {
            TimeEvent nextEvent = iterator.next();

            addAccumulator(event, allAccumulators, nextEvent);
            event = nextEvent;
        }
        addAccumulatorForLastEvent(endEvent, event, allAccumulators);


        addNotWorkingTime(allAccumulators, komTilAct);

        DateTime startAt = findStartTime(startEvent);
        calculateIntervalFromStartDay(allAccumulators, startAt);
        return allAccumulators;
    }

    private DateTime findStartTime(TimeEvent startEvent) {
        if (startEvent.getActivity().getDefaultType() == START) {
            return startEvent.getDateTime().withTimeAtStartOfDay().plusHours(8);
        } else {
            return DateUtil.normalize(startEvent.getDateTime());
        }
    }

    private List<TimeEvent> filterEventBlock(List<TimeEvent> timeEventsForDate, Activity.DefaultActivities start, Activity.DefaultActivities end) {
        List<TimeEvent> result = new ArrayList<>();

        //First should be START
        if (timeEventsForDate.get(0).getActivity().getDefaultType() != start) {
            return new ArrayList<>();
        }

        for (TimeEvent timeEvent : timeEventsForDate) {
            result.add(timeEvent);
            if (timeEvent.getActivity().getDefaultType() == end) {
                break;
            }
        }

        timeEventsForDate.removeAll(result);

        return result;
    }

    private void addAccumulator(TimeEvent event, List<WorkTimeAccumulator> allAccumulators, TimeEvent nextEvent) {
        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();
        accumulator.activity = event.getActivity();

        accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), nextEvent.getDateTime()));
        addToList(allAccumulators, accumulator);
    }

    private void addAccumulatorForLastEvent(TimeEvent gikk, TimeEvent event, List<WorkTimeAccumulator> allAccumulators) {
        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();
        accumulator.activity = event.getActivity();

        if (gikk != null) {
            accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), gikk.getDateTime()));
            addToList(allAccumulators, accumulator);
        } else {
            accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), null));
            accumulator.missingEnd = true;
            addToList(allAccumulators, accumulator);
        }

    }

    private void calculateIntervalFromStartDay(List<WorkTimeAccumulator> allAccumulators, DateTime startAt) {
        for (WorkTimeAccumulator accumulator : allAccumulators) {
            startAt = accumulator.calculateTotalIntervalStartingAt(startAt);
        }

    }

    private void addNotWorkingTime(List<WorkTimeAccumulator> allAccumulators, int komTilAct) {
        int forhver = komTilAct / allAccumulators.size();
        int rest = komTilAct % allAccumulators.size();

        log.info(String.format("Each accumulator will get %s extra minuets. %s min is rest and will not be added", forhver, rest));

        for (WorkTimeAccumulator accumulator : allAccumulators) {
            accumulator.extraMin = forhver;
        }
    }


    private void addActivities(List<TimeEvent> timeEventsForDate, Map<String, Activity> activitiesByKey) {
        for (TimeEvent timeEvent : timeEventsForDate) {
            timeEvent.setActivity(activitiesByKey);
        }
    }


    private void addToList(List<WorkTimeAccumulator> accumulators, WorkTimeAccumulator accumulator) {
        if (accumulator.missingEnd) {
            accumulators.add(accumulator);
            return;
        }

        for (WorkTimeAccumulator a : accumulators) {
            if (!a.missingEnd && a.activity.getKeyString().equals(accumulator.activity.getKeyString())) {
                a.plus(accumulator);
                return;
            }
        }

        //dersom den ikke allerede var der legges den til
        accumulators.add(accumulator);
    }


    private TimeEvent findDefault(Activity.DefaultActivities type, List<TimeEvent> timeEventsForDate) {
        int index = -1;
        for (int i = 0; i < timeEventsForDate.size(); i++) {
            if (timeEventsForDate.get(i).getActivity().getDefaultType() == type) {
                index = i;
            }
        }

        if (index < 0) {
            return null;
        }

        TimeEvent timeEvent = timeEventsForDate.get(index);
        timeEventsForDate.remove(index);

        return timeEvent;
    }

}
