package com.marius.komgikk.domain.summary;

import com.google.common.base.Preconditions;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.json.JsonTimeSummaryActivity;
import com.marius.komgikk.domain.json.JsonTimeSummaryFromTo;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.marius.komgikk.util.DateUtil.normalize;

public class WorkTimeAccumulator {

    public boolean missingEnd = false;

    public Activity activity;

    public int workMinutes;
    public int extraMin;

    public DateTime calculatedFrom;
    public DateTime calculatedTo;

    public DateTime normalizedFrom;
    public DateTime normalizedTo;

    private List<TimeInterval> intervals = new ArrayList<>();


    public double getHours() {
        return getNormalizedMinutes() / 60d;
    }

    public int getNormalizedMinutes() {
        return normalizedTo.getMinuteOfDay() - normalizedFrom.getMinuteOfDay();
    }

    public void addTimeInterval(TimeInterval timeInterval) {
        intervals.add(timeInterval);
        workMinutes +=timeInterval.minutes;
    }

    public void plus(WorkTimeAccumulator accumulator) {
        Preconditions.checkArgument(this.activity.getKeyString().equals(accumulator.activity.getKeyString()));

        this.intervals.addAll(accumulator.intervals);
        this.workMinutes += accumulator.workMinutes;
    }

    public DateTime calculateTotalIntervalStartingAt(DateTime startAt) {

        //calculatedFrom and normalized from should always be equals.
        calculatedFrom = startAt;
        normalizedFrom = normalize(startAt);


        calculatedTo = normalizedFrom.plusMinutes(workMinutes + extraMin);
        normalizedTo = normalize(calculatedTo);

        return normalizedTo;
    }



    public JsonTimeSummaryActivity forJson() {
        JsonTimeSummaryActivity jsonTimeSummaryActivity = new JsonTimeSummaryActivity();
        jsonTimeSummaryActivity.activityName = activity.getName();
        jsonTimeSummaryActivity.sap = activity.getSap();
        jsonTimeSummaryActivity.normalizedFrom = normalizedFrom.toString("HH:mm");
        jsonTimeSummaryActivity.normalizedTo = normalizedTo.toString("HH:mm");

        jsonTimeSummaryActivity.hours = Double.toString(getHours());

        for (TimeInterval timeInterval : intervals) {
            JsonTimeSummaryFromTo fromTo = new JsonTimeSummaryFromTo();
            jsonTimeSummaryActivity.actualTimes.add(fromTo);
            fromTo.from = timeInterval.fra.toString("HH:mm");
            fromTo.to = timeInterval.til != null ? timeInterval.til.toString("HH:mm") : null;
        }

        return jsonTimeSummaryActivity;

    }

    @Override
    public String toString() {
        return String.format("Acc for %s. WorkMinutes: %d. ExtraMin: %d. Calculated; %s - %s. Normalized: %s - %s",
                activity.getName(),
                workMinutes,
                extraMin,
                calculatedFrom != null ? calculatedFrom.toString("HH:mm") : null,
                calculatedTo != null ? calculatedTo.toString("HH:mm") : null,
                normalizedFrom != null ? normalizedFrom.toString("HH:mm") : null,
                normalizedTo != null ? normalizedTo.toString("HH:mm") : null
        );
    }





}
