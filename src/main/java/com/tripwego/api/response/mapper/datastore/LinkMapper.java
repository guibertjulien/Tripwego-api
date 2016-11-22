package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.Link;

/**
 * Created by JG on 17/11/16.
 */
public class LinkMapper {

    public String map(Object property) {
        String result = "";
        if (property != null) {
            final Link link = (Link) property;
            result = link.getValue();
        }
        return result;
    }
}
