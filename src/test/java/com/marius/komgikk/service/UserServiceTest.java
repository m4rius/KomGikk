package com.marius.komgikk.service;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.marius.komgikk.domain.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by marius on 31.08.14.
 */
public class UserServiceTest {

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
    public void testStore() {
        UserService userService = new UserService();

        User user = userService.store(
                "{\"command\":\"store\", \"entity\":\"user\", \"params\": " +
                "{\"username\":\"user1\", \"name\":\"nils\" , \"password\":\"pass1\"," +
                " \"email\":\"nils.nilsen@gmail.com\"}}");

        Assert.assertEquals("user1", user.getUsername());
        Assert.assertEquals("nils", user.getName());

    }
}
