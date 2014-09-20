package com.marius.komgikk.rest;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.marius.komgikk.domain.JsonTimeEvent;
import com.marius.komgikk.domain.TimeEvent;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
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

import static com.marius.komgikk.rest.DataStoreTestUtil.*;
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

        InputStream is = (InputStream) response.getEntity();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(is))) {
            for (int c; (c = rdr.read()) != -1;) {
                sb.append((char) c);

            }
        }

        String result = sb.toString();

        assertTrue(result.contains("\"specialEvent\":\"START\""));
        assertTrue(result.contains("key"));
        assertTrue(result.contains("time"));
        assertTrue(result.contains("date"));
    }
}
