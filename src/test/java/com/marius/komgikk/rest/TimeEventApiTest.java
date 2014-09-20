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
