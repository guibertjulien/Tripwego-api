package com.example.tripwegoapi.response.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.Tag;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;


public class TagMapper {

    public Tag map(Entity entity) {
        final Tag tag = new Tag();
        tag.setId(KeyFactory.keyToString(entity.getKey()));
        tag.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        tag.setType(String.valueOf(entity.getProperty(Constants.TYPE)));
        tag.setColor(String.valueOf(entity.getProperty(Constants.COLOR)));
        tag.setIconType(String.valueOf(entity.getProperty(Constants.ICON_TYPE)));
        return tag;
    }
}
