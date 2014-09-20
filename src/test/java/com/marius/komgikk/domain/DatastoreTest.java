package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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

        new KomGikkUser("user1", "Nils", "test@test.no").store();
        assertEquals(1, ds.prepare(new Query(KomGikkUser.kind)).countEntities(withLimit(10)));
        new KomGikkUser("user2", "Ola", "test2@test.no").store();
        assertEquals(2, ds.prepare(new Query(KomGikkUser.kind)).countEntities(withLimit(10)));

        KomGikkUser user = KomGikkUser.get("user1");
        assertEquals("Nils", user.getName());
    }

    @Test
    public void testActivity() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        KomGikkUser user1 = new KomGikkUser("u1", "Nils", "test1@test.no").store();
        KomGikkUser user2 = new KomGikkUser("u2", "Ola", "test2@test.no").store();

        new Activity(user1, "Project1", "xx").store();
        new Activity(user1, "Project2", "yy").store();
        new Activity(user2, "Project3", "zz").store();

        assertEquals(3, ds.prepare(new Query(Activity.kind)).countEntities(withLimit(10)));

        List<JsonActivity> user1sActivities = Activity.getForJson(user1);
        assertEquals(2, user1sActivities.size());

        //TODO: test at listen ikke inneholder project3;
    }
}
