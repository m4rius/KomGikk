package com.marius.komgikk.service;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.marius.komgikk.domain.KomGikkUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class KomGikkUserServiceTest {

    private LocalServiceTestHelper helper;

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
    public void testGetCurrentUser() {
        UserService userService = new UserService();

        KomGikkUser currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals("10", currentUser.getUsername());
        assertEquals("test@test.com", currentUser.getEmail());
        assertNull(currentUser.getName());
    }
}
