package com.marius.komgikk.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;

import java.util.HashMap;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

public class DataStoreTestUtil {

    public static LocalServiceTestHelper getLocalServiceTestHelperWithUser() {
        HashMap<String, Object> envAttr = new HashMap<String, Object>();
        envAttr.put("com.google.appengine.api.users.UserService.user_id_key", "10");

        return new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
                .setEnvIsAdmin(true)
                .setEnvEmail("test@test.com")
                .setEnvAuthDomain("domain")
                .setEnvAttributes(envAttr)
                .setEnvIsLoggedIn(true);

    }

    public static KomGikkUser prepareAndStoreDefaultUser() {
        KomGikkUser user = new KomGikkUser("10", "test@test.no");
        user.store();
        Activity.storeDefaults(user);
        return user;
    }

    public static void assertNumberOfEntitiesStored(String kind, int number) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        assertEquals(number, ds.prepare(new Query(kind)).countEntities(withLimit(10)));
    }
}
