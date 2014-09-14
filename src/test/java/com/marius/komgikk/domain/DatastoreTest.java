package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

public class DatastoreTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setup() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();

    }

    @Test
    public void testUser() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        new KomGikkUser("user1", "Nils").store();
        assertEquals(1, ds.prepare(new Query(KomGikkUser.kind)).countEntities(withLimit(10)));
        new KomGikkUser("user2", "Ola").store();
        assertEquals(2, ds.prepare(new Query(KomGikkUser.kind)).countEntities(withLimit(10)));

        KomGikkUser user = KomGikkUser.get("user1");
        assertEquals("Nils", user.getName());
    }

    @Test
    public void testActivity() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        KomGikkUser user1 = new KomGikkUser("u1", "Nils").store();
        KomGikkUser user2 = new KomGikkUser("u2", "Ola").store();

        new Activity(user1, "Project1").store();
        new Activity(user1, "Project2").store();
        new Activity(user2, "Project3").store();

        assertEquals(3, ds.prepare(new Query(Activity.kind)).countEntities(withLimit(10)));

        List<Activity> user1sActivities = Activity.get(user1);
        assertEquals(2, user1sActivities.size());

        //TODO: test at listen ikke inneholder project3;
    }

    @Test
    public void testTimeEvent() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        KomGikkUser user1 = new KomGikkUser("u1", "Nils").store();
        Activity a1 = new Activity(user1, "Project1").store();

        new TimeEvent(user1, DateTime.now(), a1).store();

        assertEquals(1, ds.prepare(new Query(TimeEvent.kind)).countEntities(withLimit(10)));

        List<TimeEvent> all = TimeEvent.get(user1);


        assertEquals(1, all.size());



    }


}
