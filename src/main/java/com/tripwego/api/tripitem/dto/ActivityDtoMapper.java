package com.tripwego.api.tripitem.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.tripitem.Activity;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 25/12/16.
 */
public class ActivityDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(ActivityDtoMapper.class.getName());

    public Activity map(Entity entity, List<Step> steps) {
        LOGGER.info("--> ActivityMapper.map - START");
        final Activity activity = new Activity();
        activity.setId(KeyFactory.keyToString(entity.getKey()));
        activity.getSteps().addAll(steps);
        LOGGER.info("--> ActivityMapper.map - END");
        return activity;
    }
}
