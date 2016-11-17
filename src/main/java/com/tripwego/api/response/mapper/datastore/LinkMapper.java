package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.Link;

/**
 * Created by JG on 17/11/16.
 */
public class LinkMapper {

    public com.tripwego.dto.datastore.Link map(Link link) {
        final com.tripwego.dto.datastore.Link result = new com.tripwego.dto.datastore.Link();
        result.setValue(link.getValue());
        return result;
    }
}
