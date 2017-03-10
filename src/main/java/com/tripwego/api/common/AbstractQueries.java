package com.tripwego.api.common;

import com.google.appengine.api.datastore.*;

import java.util.List;

/**
 * Created by JG on 19/02/17.
 */
public abstract class AbstractQueries {

    protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public List<Entity> find(Entity parent, String kind) {
        final Query query = new Query(kind).setAncestor(parent.getKey());
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }
}
