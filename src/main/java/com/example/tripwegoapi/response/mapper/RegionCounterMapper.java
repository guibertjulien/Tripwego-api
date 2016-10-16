package com.example.tripwegoapi.response.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.RegionCounter;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;


public class RegionCounterMapper {

    public static final String CODE = "code";
    public static final String COUNTER = "counter";

    public RegionCounter map(Entity entity) {
        final RegionCounter regionCounter = new RegionCounter();
        regionCounter.setId(KeyFactory.keyToString(entity.getKey()));
        regionCounter.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        regionCounter.setCode(String.valueOf(entity.getProperty(CODE)));
        regionCounter.setCounter((Long) entity.getProperty(COUNTER));
        return regionCounter;
    }
}
