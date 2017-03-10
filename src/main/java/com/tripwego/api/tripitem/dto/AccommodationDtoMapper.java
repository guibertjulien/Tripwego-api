package com.tripwego.api.tripitem.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.tripitem.Accommodation;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 25/12/16.
 */
public class AccommodationDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(AccommodationDtoMapper.class.getName());

    public Accommodation map(Entity entity, List<Step> steps) {
        LOGGER.info("--> AccommodationMapper.map - START");
        final Accommodation accommodation = new Accommodation();
        accommodation.setId(KeyFactory.keyToString(entity.getKey()));
        accommodation.getSteps().addAll(steps);
        LOGGER.info("--> AccommodationMapper.map - END");
        return accommodation;
    }
}
