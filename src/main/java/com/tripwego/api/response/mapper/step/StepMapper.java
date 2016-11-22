package com.tripwego.api.response.mapper.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.api.response.mapper.datastore.*;
import com.tripwego.domain.Step;

import java.util.Date;
import java.util.logging.Logger;

public class StepMapper {

    private static final Logger LOGGER = Logger.getLogger(StepMapper.class.getName());

    private final PostalAddressMapper postalAddressMapper;
    private final CategoryMapper categoryMapper;
    private final LinkMapper linkMapper;
    private final RatingMapper ratingMapper;
    private final GeoPtMapper geoPtMapper;

    public StepMapper(PostalAddressMapper postalAddressMapper, CategoryMapper categoryMapper, LinkMapper linkMapper, RatingMapper ratingMapper, GeoPtMapper geoPtMapper) {
        this.postalAddressMapper = postalAddressMapper;
        this.categoryMapper = categoryMapper;
        this.linkMapper = linkMapper;
        this.ratingMapper = ratingMapper;
        this.geoPtMapper = geoPtMapper;
    }

    public Step map(Entity entity) {
        LOGGER.info("--> StepMapper.map - START");
        final Step step = new Step();
        step.setId(KeyFactory.keyToString(entity.getKey()));
        step.setParentId(String.valueOf(entity.getParent().getId()));
        if (entity.getProperty(Constants.PARENT_STEP_ID) != null) {
            step.setParentStepId(String.valueOf(entity.getProperty(Constants.PARENT_STEP_ID)));
        }
        step.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        if (entity.getProperty(Constants.ADDRESS) != null) {
            step.setAddress(postalAddressMapper.map(entity.getProperty(Constants.ADDRESS)));
        }
        if (entity.getProperty(Constants.AVOID_HIGHWAYS) != null) {
            step.setAvoidHighways((Boolean) entity.getProperty(Constants.AVOID_HIGHWAYS));
        }
        if (entity.getProperty(Constants.AVOID_TOLLS) != null) {
            step.setAvoidTolls((Boolean) entity.getProperty(Constants.AVOID_TOLLS));
        }
        if (entity.getProperty(Constants.CATEGORY) != null) {
            step.setCategory(categoryMapper.map((Category) entity.getProperty(Constants.CATEGORY)));
        }
        if (entity.getProperty(Constants.CREATED_AT) != null) {
            step.setCreatedAt((Date) entity.getProperty(Constants.CREATED_AT));
        }
        if (entity.getProperty(Constants.DAY_IN) != null) {
            step.setDayIn((Integer) entity.getProperty(Constants.DAY_IN));
        }
        if (entity.getProperty(Constants.DAY_OUT) != null) {
            step.setDayOut((Integer) entity.getProperty(Constants.DAY_OUT));
        }
        if (entity.getProperty(Constants.DESCRIPTION) != null) {
            step.setDescription(String.valueOf(entity.getProperty(Constants.DESCRIPTION)));
        }
        if (entity.getProperty(Constants.GEO_PT) != null) {
            step.setGeoPt(geoPtMapper.map((GeoPt) entity.getProperty(Constants.GEO_PT)));
        }
        if (entity.getProperty(Constants.INDEX_ON_ROAD_MAP) != null) {
            step.setIndexOnRoadMap((Integer) entity.getProperty(Constants.INDEX_ON_ROAD_MAP));
        }
        if (entity.getProperty(Constants.INFO_LINK) != null) {
            step.setInfoLink(linkMapper.map((Link) entity.getProperty(Constants.INFO_LINK)));
        }
        if (entity.getProperty(Constants.URL_PHOTO) != null) {
            step.setUrlPhoto(linkMapper.map((Link) entity.getProperty(Constants.URL_PHOTO)));
        }
        if (entity.getProperty(Constants.PLACE_CATEGORY) != null) {
            step.setPlaceCategory(String.valueOf(entity.getProperty(Constants.PLACE_CATEGORY)));
        }
        if (entity.getProperty(Constants.PLACE_TYPE) != null) {
            step.setPlaceType(String.valueOf(entity.getProperty(Constants.PLACE_TYPE)));
        }
        if (entity.getProperty(Constants.PERIOD_CATEGORY) != null) {
            step.setPeriodCategory(String.valueOf(entity.getProperty(Constants.PERIOD_CATEGORY)));
        }
        if (entity.getProperty(Constants.PERIOD_TYPE) != null) {
            step.setPeriodType(String.valueOf(entity.getProperty(Constants.PERIOD_TYPE)));
        }
        if (entity.getProperty(Constants.RATING) != null) {
            step.setRating(ratingMapper.map((Rating) entity.getProperty(Constants.RATING)));
        }
        if (entity.getProperty(Constants.UPDATED_AT) != null) {
            step.setUpdatedAt((Date) entity.getProperty(Constants.UPDATED_AT));
        }
        if (entity.getProperty(Constants.WAY_TYPE) != null) {
            step.setWayType(String.valueOf(entity.getProperty(Constants.WAY_TYPE)));
        }
        LOGGER.info("--> StepMapper.map - END");
        return step;
    }
}
