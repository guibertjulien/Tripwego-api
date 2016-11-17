package com.tripwego.api.request.mapper.step;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.Constants;
import com.tripwego.api.request.mapper.datastore.*;
import com.tripwego.dto.Step;

import java.util.Date;
import java.util.logging.Logger;

public class StepEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(StepEntityMapper.class.getName());

    private final CategoryDatastoreMapper categoryDatastoreMapper;
    private final GeoPtDatastoreMapper geoPtDatastoreMapper;
    private final LinkDatastoreMapper linkDatastoreMapper;
    private final RatingDatastoreMapper ratingDatastoreMapper;
    private final PostalAddressDatastoreMapper postalAddressDatastoreMapper;

    public StepEntityMapper(CategoryDatastoreMapper categoryDatastoreMapper, GeoPtDatastoreMapper geoPtDatastoreMapper, LinkDatastoreMapper linkDatastoreMapper, RatingDatastoreMapper ratingDatastoreMapper, PostalAddressDatastoreMapper postalAddressDatastoreMapper) {
        this.categoryDatastoreMapper = categoryDatastoreMapper;
        this.geoPtDatastoreMapper = geoPtDatastoreMapper;
        this.linkDatastoreMapper = linkDatastoreMapper;
        this.ratingDatastoreMapper = ratingDatastoreMapper;
        this.postalAddressDatastoreMapper = postalAddressDatastoreMapper;
    }

    public Entity map(Step step, Entity parent) {
        LOGGER.info("--> StepEntityMapper.map - START");
        Entity entity = new Entity(Constants.KIND_STEP, parent.getKey());
        entity.setProperty(Constants.PARENT_STEP_ID, step.getParentStepId());
        entity.setProperty(Constants.NAME, step.getName());
        entity.setProperty(Constants.ADDRESS, postalAddressDatastoreMapper.map(step.getAddress()));
        entity.setProperty(Constants.CATEGORY, categoryDatastoreMapper.map(step.getCategory()));
        entity.setProperty(Constants.DAY_IN, step.getDayIn());
        entity.setProperty(Constants.DAY_OUT, step.getDayOut());
        entity.setProperty(Constants.DESCRIPTION, step.getDescription());
        entity.setProperty(Constants.GEO_PT, geoPtDatastoreMapper.map(step.getGeoPt()));
        entity.setProperty(Constants.INFO_LINK, linkDatastoreMapper.map(step.getInfoLink()));
        entity.setProperty(Constants.URL_PHOTO, linkDatastoreMapper.map(step.getUrlPhoto()));
        entity.setProperty(Constants.PLACE_CATEGORY, step.getPlaceCategory());
        entity.setProperty(Constants.PLACE_TYPE, step.getPlaceType());
        entity.setProperty(Constants.PERIOD_CATEGORY, step.getPeriodCategory());
        entity.setProperty(Constants.PERIOD_TYPE, step.getPeriodType());
        entity.setProperty(Constants.RATING, ratingDatastoreMapper.map(step.getRating()));
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
