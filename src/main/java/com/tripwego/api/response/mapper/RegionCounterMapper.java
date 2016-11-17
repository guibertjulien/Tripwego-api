package com.tripwego.api.response.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.api.Constants;
import com.tripwego.dto.RegionCounter;

import java.util.logging.Logger;


public class RegionCounterMapper {

    private static final Logger LOGGER = Logger.getLogger(RegionCounterMapper.class.getName());

    public RegionCounter map(Entity entity) {
        LOGGER.info("--> RegionCounterMapper.map - START");
        final RegionCounter regionCounter = new RegionCounter();
        regionCounter.setId(KeyFactory.keyToString(entity.getKey()));
        regionCounter.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        regionCounter.setCode(String.valueOf(entity.getProperty(Constants.CODE)));
        regionCounter.setCounter((Integer) entity.getProperty(Constants.COUNTER));
        LOGGER.info("--> RegionCounterMapper.map - END");
        return regionCounter;
    }
}
