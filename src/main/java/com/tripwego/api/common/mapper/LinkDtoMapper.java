package com.tripwego.api.common.mapper;

import com.google.appengine.api.datastore.Link;

/**
 * Created by JG on 17/11/16.
 */
public class LinkDtoMapper {

    public String map(Object property) {
        String result = "";
        if (property != null) {
            final Link link = (Link) property;
            result = link.getValue();
        }
        return result;
    }
}
