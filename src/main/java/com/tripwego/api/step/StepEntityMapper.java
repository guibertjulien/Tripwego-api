package com.tripwego.api.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.GeoPtEntityMapper;
import com.tripwego.api.utils.Strings;
import com.tripwego.dto.step.Step;

import java.util.Date;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

public class StepEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(StepEntityMapper.class.getName());

    private final GeoPtEntityMapper geoPtEntityMapper;

    public StepEntityMapper(GeoPtEntityMapper geoPtEntityMapper) {
        this.geoPtEntityMapper = geoPtEntityMapper;
    }

    public Entity map(Step step, Entity parent) {
        LOGGER.info("--> StepEntityMapper.map - START");
        Entity entity = new Entity(KIND_STEP, parent.getKey());
        entity.setProperty(PARENT_TRIP_ITEM_ID, parent.getKey().getId());
        entity.setProperty(NAME, step.getName());
        entity.setProperty(DESCRIPTION, new Text(Strings.nullToEmpty(step.getDescription())));
        if (step.getAddress() != null) {
            entity.setProperty(ADDRESS, new PostalAddress(step.getAddress()));
        }
        if (step.getCategory() != null) {
            entity.setProperty(CATEGORY, new Category(step.getCategory()));
        }
        if (step.getType() != null) {
            entity.setProperty(TYPE, new Category(step.getType()));
        }
        entity.setProperty(DAY_IN, step.getDayIn());
        if (step.getLocation() != null) {
            entity.setProperty(GEO_PT, geoPtEntityMapper.map(step.getLocation()));
        }
        if (step.getInfoLink() != null) {
            entity.setProperty(INFO_LINK, new Link(step.getInfoLink()));
        }
        if (step.getUrlPhoto() != null) {
            if (!step.getUrlPhoto().contains(PHOTO_SERVICE_ERROR_FRAGMENT)) {
                entity.setProperty(URL_PHOTO, new Link(step.getUrlPhoto()));
            }
        }
        entity.setProperty(PLACE_TYPE, step.getPlaceType());
        entity.setProperty(RATING, new Rating(step.getRating()));
        entity.setProperty(WAY_TYPE, step.getWayType());
        entity.setProperty(WAY_TYPE_OLD, step.getWayTypeOld());
        entity.setProperty(AVOID_HIGHWAYS, step.isAvoidHighways());
        entity.setProperty(AVOID_TOLLS, step.isAvoidTolls());
        entity.setProperty(CREATED_AT, new Date());
        entity.setProperty(UPDATED_AT, new Date());
        entity.setProperty(INDEX_ON_ROAD_MAP, step.getIndexOnRoadMap());
        entity.setProperty(BUDGET, step.getBudget());
        entity.setProperty(PRICE, step.getPrice());
        entity.setProperty(STEP_LEVEL, step.getLevel());
        LOGGER.info("--> StepEntityMapper.map - END");
        return entity;
    }
}
