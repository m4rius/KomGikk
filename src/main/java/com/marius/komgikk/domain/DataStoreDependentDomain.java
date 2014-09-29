package com.marius.komgikk.domain;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public abstract class DataStoreDependentDomain {

    private transient DatastoreService dataStore;

    protected DatastoreService getDataStore() {
        if (dataStore == null) {
            dataStore = DatastoreServiceFactory.getDatastoreService();
        }
        return dataStore;
    }
}
