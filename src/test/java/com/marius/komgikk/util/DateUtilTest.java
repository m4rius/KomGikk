package com.marius.komgikk.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    @Test
    public void testStartOfWeek() {
        assertEquals(
                new DateTime(2014, 9, 29, 0, 0),
                DateUtil.getStartOfWeek(40, 2014));

        assertEquals(
                new DateTime(2013, 1, 7, 0, 0),
                DateUtil.getStartOfWeek(2, 2013)
        );
    }

    @Test
    public void testEndOfWeek() {
        assertEquals(
                new DateTime(2014, 10, 5, 23, 59, 59, 999),
                DateUtil.getEndOfWeek(40, 2014)
        );
    }

    @Test
    public void testToMidnight() {
        DateTime toMidnight = new DateTime(2014, 10, 10, 12, 56);

        assertEquals(
                new DateTime(2014, 10, 10, 0, 0),
                DateUtil.toMidnight(toMidnight)
        );
    }

    @Test
    public void testAt() {
        DateTime dateTime = new DateTime(2014, 10, 10, 7, 34);

        assertEquals(
                new DateTime(2014, 10, 10, 8, 0),
                DateUtil.at(dateTime, 8, 0)
        );
    }
}
