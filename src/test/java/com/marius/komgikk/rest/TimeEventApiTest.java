package com.marius.komgikk.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.domain.json.JsonActivity;
import com.marius.komgikk.domain.json.JsonKeyInput;
import com.marius.komgikk.domain.json.JsonTimeEvent;
import junit.framework.Assert;
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
        KomGikkUser komGikkUser = prepareAndStoreDefaultUser();

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query(Activity.kind).setAncestor(komGikkUser.getKey());

        Activity startDay = null;
        for (com.google.appengine.api.datastore.Entity entity : ds.prepare(q).asIterable()) {
            Activity activity = Activity.from(entity, komGikkUser);
            if (activity.getDefaultType() != null && activity.getDefaultType() == Activity.DefaultActivities.START) {
                startDay = activity;
            }
        }

        Assert.assertNotNull(startDay);

        JsonKeyInput post = new JsonKeyInput();
        post.key = startDay.getKeyString();

        Response response = target("timeevent").request().post(Entity.json(new Gson().toJson(post)));

        assertNumberOfEntitiesStored(TimeEvent.kind, 1);

        String result = readResponse(response);

        //TODO: Dårlig sammenligning
        assertTrue(result.contains("activity"));
        assertTrue(result.contains("key"));
        assertTrue(result.contains("time"));
        assertTrue(result.contains("date"));
    }

    @Test
    public void testList() throws IOException {
        KomGikkUser user = prepareAndStoreDefaultUser();
        List<Activity> activities = Activity.allCurrent(user);

        DateTime now = DateTime.now();

        //gårsdagens, skal ikke hentes i list
        new TimeEvent(user, now.minusDays(1).minusHours(2), activities.get(0)).store();
        new TimeEvent(user, now.minusDays(1).minusHours(1), activities.get(1)).store();
        new TimeEvent(user, now.minusDays(1), activities.get(1)).store();


        new TimeEvent(user, now.minusHours(2), activities.get(0)).store();
        new TimeEvent(user, now.minusHours(1), activities.get(1)).store();
        new TimeEvent(user, now, activities.get(1)).store();

        Response response = target("timeevent/list").request().get();

        String result = readResponse(response);

        List<JsonTimeEvent> events = new Gson().fromJson(result, new TypeToken<List<JsonTimeEvent>>() {}.getType());

        assert events != null;
        assertEquals(3, events.size());
    }

    @Test
    public void testUpdateAllUpdateAndDelete() throws IOException {
        KomGikkUser user = prepareAndStoreDefaultUser();
        List<Activity> activities = Activity.allCurrent(user);

        DateTime now = DateTime.now();
        JsonTimeEvent json1 = new TimeEvent(user, now.minusHours(2), activities.get(0)).store().forJson();
        JsonTimeEvent json2 = new TimeEvent(user, now.minusHours(1), activities.get(1)).store().forJson();
        JsonTimeEvent json3 = new TimeEvent(user, now, activities.get(1)).store().forJson();

        JsonActivity jsonActivity = new JsonActivity();
        jsonActivity.key = activities.get(2).getKeyString();

        json2.activity = jsonActivity;
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
            if (event.activity.key.equals(json2.activity.key)) {
                foundNewActivity = true;
            }
        }

        assertTrue(foundNewActivity);
    }

    @Test
    public void testUpdateAllNew() throws IOException {
        KomGikkUser user = prepareAndStoreDefaultUser();
        List<Activity> activities = Activity.allCurrent(user);

        DateTime now = DateTime.now();
        JsonTimeEvent json1 = new TimeEvent(user, now.minusHours(2), activities.get(0)).store().forJson();
        JsonTimeEvent json2 = new TimeEvent(user, now.minusHours(1), activities.get(1)).store().forJson();

        JsonActivity jsonActivity = new JsonActivity();
        jsonActivity.key = activities.get(2).getKeyString();
        JsonTimeEvent newEvent = new JsonTimeEvent();
        newEvent.activity = jsonActivity;
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
