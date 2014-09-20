package com.marius.komgikk.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.JsonActivity;
import com.marius.komgikk.domain.KomGikkUser;
import junit.framework.Assert;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import java.util.HashMap;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

public class ActivityApiTest extends JerseyTest {

    private LocalServiceTestHelper helper;


    @Override
    protected Application configure() {
        return new ResourceConfig(ActivityApi.class);
    }

    @Before
    public void setup() {
        HashMap<String, Object> envAttr = new HashMap<String, Object>();
        envAttr.put("com.google.appengine.api.users.UserService.user_id_key", "10");

        helper =
                new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
                        .setEnvIsAdmin(true)
                        .setEnvEmail("test@test.com")
                        .setEnvAuthDomain("domain")
                        .setEnvAttributes(envAttr)
                        .setEnvIsLoggedIn(true);

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
        Activity activity = new Activity(user, "aktivitet", "sap");
        activity.store();

        String expectedJson = "{\"key\":\"" + activity.getKeyString() + "\",\"name\":\"aktivitet\",\"sap\":\"sap\"}";
        String result = target("activity/" + activity.getKeyString()).request().get(String.class);

        Assert.assertEquals(expectedJson, result);

    }

    @Test
    public void testStore() {
        KomGikkUser user = new KomGikkUser("10", "test@test.no");
        user.store();

        JsonActivity post = new JsonActivity();
        post.name = "aktivitet";
        post.sap = "sap";

        target("activity").request().post(Entity.json(new Gson().toJson(post)));

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        assertEquals(1, ds.prepare(new Query(Activity.kind)).countEntities(withLimit(10)));
    }

    @Test
    public void testDelete() {
        KomGikkUser user = new KomGikkUser("10", "test@test.no");
        user.store();
        Activity activity = new Activity(user, "aktivitet", "sap");
        activity.store();

        JsonActivity post = new JsonActivity();
        post.action = "delete";
        post.name = "aktivitet";
        post.sap = "sap";
        post.key = activity.getKeyString();

        target("activity").request().post(Entity.json(new Gson().toJson(post)));

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        String state = (String) ds.prepare(new Query(Activity.kind)).asSingleEntity().getProperty("state");

        assertEquals(Activity.ActivityState.HISTORIC.name(), state);

    }
}
