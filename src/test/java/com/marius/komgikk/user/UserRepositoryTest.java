package com.marius.komgikk.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

/**
 * Created by marius on 30.08.14.
 */
public class UserRepositoryTest {

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
    public void testInsert1() throws EntityNotFoundException {
        doTest();
    }

    @Test
    public void testInsert2() throws EntityNotFoundException {
        doTest();
    }

    private void doTest() throws EntityNotFoundException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        UserRepository userRepository = new UserRepository();

        User u1 = new User("user1", "Nils");
        User u2 = new User("user2", "Ola");

        userRepository.storeUser(u1);
        userRepository.storeUser(u2);

        assertEquals(2, ds.prepare(new Query(User.kind)).countEntities(withLimit(10)));

        User n1_1 = userRepository.getUser("user1");
        assertEquals("Nils", u1.getName());

    }
}
