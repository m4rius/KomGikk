package com.marius.komgikk.rest;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UserApiTest {


    private  LocalServiceTestHelper helper;

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
        UserApi userApi = new UserApi();
        String currentUser = userApi.getCurrentUser();
        String expected = "{\"username\":\"10\",\"email\":\"test@test.com\"}";
        assertEquals(expected, currentUser);
    }

    @Test
    public void testStore() {
        String input = "{\"username\":\"10\",\"email\":\"test@test.com\", \"name\":\"Nils\"}";
        UserApi userApi = new UserApi();
 //       userApi.store(input);
    }
}
