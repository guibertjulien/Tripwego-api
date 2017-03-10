package com.tripwego.api.tripitem.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.tripitem.Rail;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 25/12/16.
 */
public class RailDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(RailDtoMapper.class.getName());

    public Rail map(Entity entity, List<Step> steps) {
        LOGGER.info("--> RailMapper.map - START");
        final Rail rail = new Rail();
        rail.setId(KeyFactory.keyToString(entity.getKey()));
        rail.getSteps().addAll(steps);
        LOGGER.info("--> RailMapper.map - END");
        return rail;
    }
}
