package com.tripwego.api.response.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.api.Constants;
import com.tripwego.domain.Tag;

import java.util.logging.Logger;


public class TagMapper {

    private static final Logger LOGGER = Logger.getLogger(TagMapper.class.getName());

    public Tag map(Entity entity) {
        LOGGER.info("--> TagMapper.map - START");
        final Tag tag = new Tag();
        tag.setId(KeyFactory.keyToString(entity.getKey()));
        tag.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        tag.setType(String.valueOf(entity.getProperty(Constants.TYPE)));
        tag.setColor(String.valueOf(entity.getProperty(Constants.COLOR)));
        tag.setIconType(String.valueOf(entity.getProperty(Constants.ICON_TYPE)));
        LOGGER.info("--> TagMapper.map - END");
        return tag;
    }
}
