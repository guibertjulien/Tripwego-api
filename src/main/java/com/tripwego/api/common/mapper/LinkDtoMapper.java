package com.tripwego.api.common.mapper;

import com.google.appengine.api.datastore.Link;

import java.util.logging.Logger;

/**
 * Created by JG on 17/11/16.
 */
public class LinkDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(LinkDtoMapper.class.getName());

    public String map(Object property) {
        String result = "";
        try {
            final Link link = (Link) property;
            result = link.getValue();
        } catch (Exception e) {
            LOGGER.warning("--> LinkDtoMapper error : " + e.getMessage());
        }
        return result;
    }
}
