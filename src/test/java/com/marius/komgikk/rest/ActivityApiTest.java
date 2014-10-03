package com.marius.komgikk.rest;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.json.JsonActivity;
import junit.framework.Assert;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;

import static com.marius.komgikk.rest.DataStoreTestUtil.assertNumberOfEntitiesStored;
import static com.marius.komgikk.rest.DataStoreTestUtil.prepareAndStoreDefaultUser;
import static org.junit.Assert.assertEquals;

public class ActivityApiTest extends JerseyTest {

    private LocalServiceTestHelper helper = DataStoreTestUtil.getLocalServiceTestHelperWithUser();


    @Override
    protected Application configure() {
        return new ResourceConfig(ActivityApi.class);
    }

    @Before
    public void setup() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGet() {
        KomGikkUser user = new KomGikkUser("10", "test@test.no");
        user.store();
        Activity activity = new Activity(user, "aktivitet", "sap", "c1");
        activity.store();

        String expectedJson = "{\"key\":\"" + activity.getKeyString() + "\",\"name\":\"aktivitet\",\"sap\":\"sap\",\"category\":\"c1\"}";
        String result = target("activity/" + activity.getKeyString()).request().get(String.class);

        Assert.assertEquals(expectedJson, result);

    }

    @Test
    public void testStore() {
        prepareAndStoreDefaultUser();

        JsonActivity post = new JsonActivity();
        post.name = "aktivitet";
        post.sap = "sap";

        target("activity").request().post(Entity.json(new Gson().toJson(post)));

        //5 = 4 default + 1 new
        assertNumberOfEntitiesStored(Activity.kind, 5);
    }

    @Test
    public void testDelete() {
        KomGikkUser user = prepareAndStoreDefaultUser();

        Activity activity = new Activity(user, "aktivitet", "sap", "c2");
        activity.store();

        JsonActivity post = new JsonActivity();
        post.action = "delete";
        post.name = "aktivitet";
        post.sap = "sap";
        post.key = activity.getKeyString();

        target("activity").request().post(Entity.json(new Gson().toJson(post)));

        Activity stored = Activity.getByKey(post.key, user);

        assertEquals(Activity.ActivityState.HISTORIC, stored.getState());

    }
}
