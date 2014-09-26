package com.marius.komgikk.rest;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.marius.komgikk.domain.JsonTimeEvent;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.marius.komgikk.rest.DataStoreTestUtil.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TimeEventApiTest extends JerseyTest {

    private LocalServiceTestHelper helper = getLocalServiceTestHelperWithUser();

    @Before
    public void setup() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TimeEventApi.class);
    }

    @Test
    public void testStore() throws IOException {
        storeDefaultUser();

        JsonTimeEvent post = new JsonTimeEvent();
        post.specialEvent = TimeEvent.TimeEventSpecialType.START.name();

        Response response = target("timeevent").request().post(Entity.json(new Gson().toJson(post)));

        assertNumberOfEntitiesStored(TimeEvent.kind, 1);

        String result = readResponse(response);

        assertTrue(result.contains("\"specialEvent\":\"START\""));
        assertTrue(result.contains("key"));
        assertTrue(result.contains("time"));
        assertTrue(result.contains("date"));
    }

    @Test
    public void testList() throws IOException {
        KomGikkUser user = storeDefaultUser();

        DateTime now = DateTime.now();


        //gårsdagens, skal ikke hentes i list
        new TimeEvent(user, now.minusDays(1).minusHours(2), TimeEvent.TimeEventSpecialType.START).store();
        new TimeEvent(user, now.minusDays(1).minusHours(1), "activityKey").store();
        new TimeEvent(user, now.minusDays(1), TimeEvent.TimeEventSpecialType.END).store();


        new TimeEvent(user, now.minusHours(2), TimeEvent.TimeEventSpecialType.START).store();
        new TimeEvent(user, now.minusHours(1), "activityKey").store();
        new TimeEvent(user, now, TimeEvent.TimeEventSpecialType.END).store();

        Response response = target("timeevent/list").request().get();

        String result = readResponse(response);

        List<JsonTimeEvent> events = new Gson().fromJson(result, new TypeToken<List<JsonTimeEvent>>() {}.getType());

        assertEquals(3, events.size());
    }

    @Test
    public void testUpdateAllUpdateAndDelete() throws IOException {
        KomGikkUser user = storeDefaultUser();

        DateTime now = DateTime.now();
        JsonTimeEvent json1 = new TimeEvent(user, now.minusHours(2), TimeEvent.TimeEventSpecialType.START).store().forJson();
        JsonTimeEvent json2 = new TimeEvent(user, now.minusHours(1), "activityKey").store().forJson();
        JsonTimeEvent json3 = new TimeEvent(user, now, TimeEvent.TimeEventSpecialType.END).store().forJson();

        json2.activityKey = "newActivityKey";
        json3.isDeleted = true;

        List<JsonTimeEvent> put = new ArrayList<>();
        put.add(json1);
        put.add(json2);
        put.add(json3);


        Response response = target("timeevent/list").request().put(Entity.json(new Gson().toJson(put)));
        DataStoreTestUtil.assertNumberOfEntitiesStored(TimeEvent.kind, 2);

        List<JsonTimeEvent> events = new Gson().fromJson(readResponse(response), new TypeToken<List<JsonTimeEvent>>() {}.getType());

        boolean foundNewActivity = false;
        for (JsonTimeEvent event : events) {
            if (event.activityKey != null && event.activityKey.equals(json2.activityKey)) {
                foundNewActivity = true;
            }
        }

        assertTrue(foundNewActivity);
    }

    @Test
    public void testUpdateAllNew() throws IOException {
        KomGikkUser user = storeDefaultUser();

        DateTime now = DateTime.now();
        JsonTimeEvent json1 = new TimeEvent(user, now.minusHours(2), TimeEvent.TimeEventSpecialType.START).store().forJson();
        JsonTimeEvent json2 = new TimeEvent(user, now.minusHours(1), "activityKey").store().forJson();


        JsonTimeEvent newEvent = new JsonTimeEvent();
        newEvent.activityKey = "enAktvitet";
        newEvent.time = "12:00";
        newEvent.date = "12.12.2000";
        newEvent.isNew = true;

        List<JsonTimeEvent> put = new ArrayList<>();
        put.add(json1);
        put.add(json2);
        put.add(newEvent);

        target("timeevent/list").request().put(Entity.json(new Gson().toJson(put)));
        DataStoreTestUtil.assertNumberOfEntitiesStored(TimeEvent.kind, 3);
    }

    private String readResponse(Response response) throws IOException {

        InputStream is = (InputStream) response.getEntity();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(is))) {
            for (int c; (c = rdr.read()) != -1;) {
                sb.append((char) c);

            }
        }

        return sb.toString();
    }

}
