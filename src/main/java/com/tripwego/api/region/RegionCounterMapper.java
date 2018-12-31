package com.tripwego.api.region;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.region.RegionCounter;

import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;


public class RegionCounterMapper {

    private static final Logger LOGGER = Logger.getLogger(RegionCounterMapper.class.getName());

    public RegionCounter map(Entity entity) {
        LOGGER.info("--> RegionCounterMapper.map - START");
        final RegionCounter regionCounter = new RegionCounter();
        regionCounter.setId(KeyFactory.keyToString(entity.getKey()));
        regionCounter.setName(String.valueOf(entity.getProperty(NAME)));
        regionCounter.setCode(String.valueOf(entity.getProperty(CODE)));
        regionCounter.setCounter((Long) entity.getProperty(COUNTER));
        LOGGER.info("--> RegionCounterMapper.map - END");
        return regionCounter;
    }
}
