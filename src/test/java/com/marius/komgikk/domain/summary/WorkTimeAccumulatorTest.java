package com.marius.komgikk.domain.summary;

import com.marius.komgikk.util.DateUtil;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorkTimeAccumulatorTest {

    @Test
    public void testCalculateAndNormalize1Interval() {

        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();

        accumulator.addTimeInterval(new TimeInterval(
                getDateTime(8, 5),
                getDateTime(9,50)));

        accumulator.calculateTotalIntervalStartingAt(
                getDateTime(8, 0)
        );

        assertEquals(
                getDateTime(8, 0),
                accumulator.normalizedFrom
        );

        assertEquals(
                getDateTime(9, 45),
                accumulator.normalizedTo
        );
    }

    @Test
    public void testCalculateAndNormalize2Intervals() {
        WorkTimeAccumulator accumulator = new WorkTimeAccumulator();

        accumulator.addTimeInterval(new TimeInterval(
                getDateTime(8, 5),
                getDateTime(9, 50)));

        accumulator.addTimeInterval(new TimeInterval(
                getDateTime(12, 36),
                getDateTime(16, 20)
        ));

        accumulator.calculateTotalIntervalStartingAt(
                getDateTime(8, 0)
        );

        assertEquals(
                getDateTime(8, 0),
                accumulator.normalizedFrom
        );

        assertEquals(
                getDateTime(13, 30),
                accumulator.normalizedTo
        );
    }

    private DateTime getDateTime(int hour, int minutes) {
        return DateUtil.now().withHourOfDay(hour).withMinuteOfHour(minutes).withSecondOfMinute(0).withMillisOfSecond(0);
    }
}
