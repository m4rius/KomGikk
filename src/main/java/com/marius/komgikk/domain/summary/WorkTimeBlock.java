package com.marius.komgikk.domain.summary;

import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.util.DateUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static com.marius.komgikk.domain.Activity.DefaultActivities.*;

public class WorkTimeBlock {

    private Logger log = Logger.getLogger(this.getClass().getName());

    public List<WorkTimeAccumulator> accumulators = new ArrayList<>();
    public boolean normalHours;
    private Activity.DefaultActivities startActivity;
    private Activity.DefaultActivities endActivity;
    public int totalMinutes;

    public static WorkTimeBlock forNormalHours(List<TimeEvent> events) {
        return new WorkTimeBlock(events, true, START, END);
    }

    public static WorkTimeBlock forExtraHours(List<TimeEvent> events) {
        return new WorkTimeBlock(events, false, START_EXTRA, END_EXTRA);
    }

    private WorkTimeBlock(List<TimeEvent> eventsIn, boolean normalHours, Activity.DefaultActivities start, Activity.DefaultActivities end) {
        this.normalHours = normalHours;
        startActivity = start;
        endActivity = end;

        List<TimeEvent> events = new ArrayList<>();
        events.addAll(eventsIn);

        int normalWorkMinutes = 8 * 60;

        TimeEvent startEvent = findDefault(startActivity, events);
        TimeEvent endEvent = findDefault(endActivity, events);

        Iterator<TimeEvent> iterator = events.iterator();

        //Dagens første event
        TimeEvent event = iterator.next();

        //minutter fra Kom til første event startet. Skal deles ut på alle akumulators til slutt
        int komTilAct = event.getDateTime().getMinuteOfDay() - startEvent.getDateTime().getMinuteOfDay();

        while (iterator.hasNext()) {
            TimeEvent nextEvent = iterator.next();

            createAndAddAccumulator(event, nextEvent);
            event = nextEvent;

        }

        addAccumulatorForLastEvent(endEvent, event);
        addNotWorkingTime(komTilAct);

        calculateIntervalFromStartDay(findStartTime(startEvent));
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

    private void createAndAddAccumulator(TimeEvent event, TimeEvent nextEvent) {
        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();
        accumulator.activity = event.getActivity();

        accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), nextEvent.getDateTime()));
        addToList(accumulator);
    }

    private void addToList(WorkTimeAccumulator accumulator) {
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

    private void addAccumulatorForLastEvent(TimeEvent gikk, TimeEvent event) {
        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();
        accumulator.activity = event.getActivity();

        if (gikk != null) {
            accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), gikk.getDateTime()));
            addToList(accumulator);
        } else {
            accumulator.addTimeInterval(new TimeInterval(event.getDateTime(), null));
            accumulator.missingEnd = true;
            addToList(accumulator);
        }

    }

    private void addNotWorkingTime(int komTilAct) {
        int forhver = komTilAct / accumulators.size();
        int rest = komTilAct % accumulators.size();

        for (WorkTimeAccumulator accumulator : accumulators) {
            accumulator.extraMin = forhver;
        }

        WorkTimeAccumulator first = accumulators.get(0);
        first.extraMin = first.extraMin + rest;
    }

    private DateTime findStartTime(TimeEvent startEvent) {
        if (startEvent.getActivity().getDefaultType() == START) {
            return startEvent.getDateTime().withTimeAtStartOfDay().plusHours(8);
        } else {
            return DateUtil.normalize(startEvent.getDateTime());
        }
    }

    private void calculateIntervalFromStartDay(DateTime startAt) {
        for (WorkTimeAccumulator accumulator : accumulators) {
            startAt = accumulator.calculateTotalIntervalStartingAt(startAt);
        }
    }

}
