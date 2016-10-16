package com.example.tripwegoapi.request.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.Step;
import com.google.appengine.api.datastore.Entity;

import java.util.Date;
import java.util.logging.Logger;

public class StepEntityMapper {

    private static final Logger _logger = Logger.getLogger(StepEntityMapper.class.getName());

    public Entity map(Step step, Entity parent) {
        _logger.info("--> StepEntityMapper.map - START");
        Entity entity = new Entity(Constants.KIND_STEP, parent.getKey());
        entity.setProperty(Constants.PARENT_STEP_ID, step.getParentStepId());
        entity.setProperty(Constants.NAME, step.getName());
        entity.setProperty(Constants.ADDRESS, step.getAddress());
        entity.setProperty(Constants.CATEGORY, step.getCategory());
        entity.setProperty(Constants.DAY_IN, step.getDayIn());
        entity.setProperty(Constants.DAY_OUT, step.getDayOut());
        entity.setProperty(Constants.DESCRIPTION, step.getDescription());
        entity.setProperty(Constants.GEO_PT, step.getGeoPt());
        entity.setProperty(Constants.INFO_LINK, step.getInfoLink());
        entity.setProperty(Constants.URL_PHOTO, step.getUrlPhoto());
        entity.setProperty(Constants.PLACE_CATEGORY, step.getPlaceCategory());
        entity.setProperty(Constants.PLACE_TYPE, step.getPlaceType());
        entity.setProperty(Constants.PERIOD_CATEGORY, step.getPeriodCategory());
        entity.setProperty(Constants.PERIOD_TYPE, step.getPeriodType());
        entity.setProperty(Constants.RATING, step.getRating());
        entity.setProperty(Constants.UPDATED_AT, step.getUpdatedAt());
        entity.setProperty(Constants.WAY_TYPE, step.getWayType());
        entity.setProperty(Constants.AVOID_HIGHWAYS, step.isAvoidHighways());
        entity.setProperty(Constants.AVOID_TOLLS, step.isAvoidTolls());
        entity.setProperty(Constants.CREATED_AT, new Date());
        entity.setProperty(Constants.UPDATED_AT, new Date());
        // TODO use ?
        entity.setProperty(Constants.INDEX_ON_ROAD_MAP, step.getIndexOnRoadMap());
        _logger.info("--> StepEntityMapper.map - END");
        return entity;
    }
}
