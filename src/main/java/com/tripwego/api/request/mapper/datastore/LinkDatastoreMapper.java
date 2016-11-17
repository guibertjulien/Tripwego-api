package com.tripwego.api.request.mapper.datastore;

import com.google.appengine.api.datastore.Link;

/**
 * Created by JG on 17/11/16.
 */
public class LinkDatastoreMapper {

    public Link map(com.tripwego.dto.datastore.Link link) {
        return new Link(link.getValue());
    }
}
