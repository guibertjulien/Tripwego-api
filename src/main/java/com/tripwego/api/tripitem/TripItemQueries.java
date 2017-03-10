package com.tripwego.api.tripitem;

import com.google.appengine.api.datastore.*;

import java.util.List;

/**
 * Created by JG on 25/12/16.
 */
public class TripItemQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public List<Entity> findTripItem(Entity parent, String kind) {
        final Query query = new Query(kind).setAncestor(parent.getKey());
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }
}
