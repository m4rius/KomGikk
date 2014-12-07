package com.marius.komgikk.domain.summary;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class WorkTimeBlockTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setup() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private KomGikkUser user;

    private Activity kom;
    private Activity activity1;
    private Activity activity2;
    private Activity activity3;
    private Activity gikk;

    @Test
    public void testFullDay() {

        initTestData();

        List<TimeEvent> events = new ArrayList<>();
        events.add(new TimeEvent(user, getTime(7, 0), kom));
        events.add(new TimeEvent(user, getTime(8, 10), activity1));
        events.add(new TimeEvent(user, getTime(13, 50), activity2));
        events.add(new TimeEvent(user, getTime(15 ,0), gikk));

        WorkTimeBlock block = WorkTimeBlock.forNormalHours(events);

        assertEquals(2, block.accumulators.size());
        assertEquals(activity1, block.accumulators.get(0).activity);
        assertEquals(activity2, block.accumulators.get(1).activity);
    }

    private void initTestData() {
        user = new KomGikkUser("test", "test", "test@test.no").store();

        kom = Activity.defaultActivity(user, Activity.DefaultActivities.START).store();
        activity1 = new Activity(user, "a1", "", "").store();
        activity2 = new Activity(user, "a2", "", "").store();
        activity3 = new Activity(user, "a3", "", "").store();
        gikk = Activity.defaultActivity(user, Activity.DefaultActivities.END).store();

    }

    private DateTime getTime(int hour, int min) {
        return new DateTime(2000, 1, 1, hour, min);
    }
}
