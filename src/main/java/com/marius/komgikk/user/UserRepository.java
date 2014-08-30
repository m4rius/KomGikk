package com.marius.komgikk.user;

import com.google.appengine.api.datastore.*;

/**
 * Created by marius on 30.08.14.
 */
public class UserRepository {

    private DatastoreService datastoreService;

    public UserRepository( ) {
        this.datastoreService = DatastoreServiceFactory.getDatastoreService();;
    }

    public void storeUser(User user) {
        datastoreService.put(user.createEntity());
    }

    public User getUser(String username) throws EntityNotFoundException {
        Key key = KeyFactory.createKey(User.kind, username);
        Entity entity = datastoreService.get(key);

        return User.from(entity);

    }
}
