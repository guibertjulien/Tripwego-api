package com.tripwego.api.request.mapper.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.api.request.mapper.datastore.GeoPtDatastoreMapper;
import com.tripwego.domain.Step;

import java.util.Date;
import java.util.logging.Logger;

public class StepEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(StepEntityMapper.class.getName());

    private final GeoPtDatastoreMapper geoPtDatastoreMapper;

    public StepEntityMapper(GeoPtDatastoreMapper geoPtDatastoreMapper) {
        this.geoPtDatastoreMapper = geoPtDatastoreMapper;
    }

    public Entity map(Step step, Entity parent) {
        LOGGER.info("--> StepEntityMapper.map - START");
        Entity entity = new Entity(Constants.KIND_STEP, parent.getKey());
        entity.setProperty(Constants.PARENT_STEP_ID, step.getParentStepId());
        entity.setProperty(Constants.NAME, step.getName());
        entity.setProperty(Constants.ADDRESS, new PostalAddress(step.getAddress()));
        entity.setProperty(Constants.CATEGORY, new Category(step.getCategory()));
        entity.setProperty(Constants.DAY_IN, step.getDayIn());
        entity.setProperty(Constants.DAY_OUT, step.getDayOut());
        entity.setProperty(Constants.DESCRIPTION, step.getDescription());
        entity.setProperty(Constants.GEO_PT, geoPtDatastoreMapper.map(step.getGeoPt()));
        entity.setProperty(Constants.INFO_LINK, new Link(step.getInfoLink()));
        entity.setProperty(Constants.URL_PHOTO, new Link(step.getUrlPhoto()));
        entity.setProperty(Constants.PLACE_CATEGORY, step.getPlaceCategory());
        entity.setProperty(Constants.PLACE_TYPE, step.getPlaceType());
        entity.setProperty(Constants.PERIOD_CATEGORY, step.getPeriodCategory());
        entity.setProperty(Constants.PERIOD_TYPE, step.getPeriodType());
        entity.setProperty(Constants.RATING, new Rating(step.getRating()));
        entity.setProperty(Constants.WAY_TYPE, step.getWayType());
        entity.setProperty(Constants.AVOID_HIGHWAYS, step.isAvoidHighways());
        entity.setProperty(Constants.AVOID_TOLLS, step.isAvoidTolls());
        entity.setProperty(Constants.CREATED_AT, new Date());
        entity.setProperty(Constants.UPDATED_AT, new Date());
        // TODO use ?
        entity.setProperty(Constants.INDEX_ON_ROAD_MAP, step.getIndexOnRoadMap());
        LOGGER.info("--> StepEntityMapper.map - END");
        return entity;
    }
}
