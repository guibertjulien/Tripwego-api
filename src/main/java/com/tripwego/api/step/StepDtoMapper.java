package com.tripwego.api.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.*;
import com.tripwego.dto.step.Step;

import java.util.Date;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

public class StepDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(StepDtoMapper.class.getName());

    private final PostalAddressDtoMapper postalAddressDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final LinkDtoMapper linkDtoMapper;
    private final RatingDtoMapper ratingDtoMapper;
    private final LatLngDtoMapper latLngDtoMapper;

    public StepDtoMapper(PostalAddressDtoMapper postalAddressDtoMapper, CategoryDtoMapper categoryDtoMapper, LinkDtoMapper linkDtoMapper, RatingDtoMapper ratingDtoMapper, LatLngDtoMapper latLngDtoMapper) {
        this.postalAddressDtoMapper = postalAddressDtoMapper;
        this.categoryDtoMapper = categoryDtoMapper;
        this.linkDtoMapper = linkDtoMapper;
        this.ratingDtoMapper = ratingDtoMapper;
        this.latLngDtoMapper = latLngDtoMapper;
    }

    public Step map(Entity entity) {
        LOGGER.info("--> StepDtoMapper.map - START");
        final Step step = new Step();
        step.setId(KeyFactory.keyToString(entity.getKey()));
        step.setParentId(String.valueOf(entity.getParent().getId()));
        if (entity.getProperty(PARENT_TRIP_ITEM_ID) != null) {
            step.setParentTripItemId(String.valueOf(entity.getProperty(PARENT_TRIP_ITEM_ID)));
        }
        step.setName(String.valueOf(entity.getProperty(NAME)));
        if (entity.getProperty(DESCRIPTION) != null) {
            step.setDescription(((Text) entity.getProperty(DESCRIPTION)).getValue());
        }
        if (entity.getProperty(ADDRESS) != null) {
            step.setAddress(postalAddressDtoMapper.map(entity.getProperty(ADDRESS)));
        }
        if (entity.getProperty(AVOID_HIGHWAYS) != null) {
            step.setAvoidHighways((Boolean) entity.getProperty(AVOID_HIGHWAYS));
        }
        if (entity.getProperty(AVOID_TOLLS) != null) {
            step.setAvoidTolls((Boolean) entity.getProperty(AVOID_TOLLS));
        }
        if (entity.getProperty(CATEGORY) != null) {
            step.setCategory(categoryDtoMapper.map(entity.getProperty(CATEGORY)));
        }
        if (entity.getProperty(TYPE) != null) {
            step.setType(categoryDtoMapper.map(entity.getProperty(TYPE)));
        }
        if (entity.getProperty(CREATED_AT) != null) {
            step.setCreatedAt(String.valueOf((Date) entity.getProperty(CREATED_AT)));
        }
        if (entity.getProperty(DAY_IN) != null) {
            step.setDayIn((long) entity.getProperty(DAY_IN));
        }
        if (entity.getProperty(GEO_PT) != null) {
            step.setLocation(latLngDtoMapper.map((GeoPt) entity.getProperty(GEO_PT)));
        }
        if (entity.getProperty(INDEX_ON_ROAD_MAP) != null) {
            step.setIndexOnRoadMap((long) entity.getProperty(INDEX_ON_ROAD_MAP));
        }
        if (entity.getProperty(INFO_LINK) != null) {
            step.setInfoLink(linkDtoMapper.map((Link) entity.getProperty(INFO_LINK)));
        }
        if (entity.getProperty(URL_PHOTO) != null) {
            step.setUrlPhoto(linkDtoMapper.map((Link) entity.getProperty(URL_PHOTO)));
        }
        if (entity.getProperty(PLACE_TYPE) != null) {
            step.setPlaceType(String.valueOf(entity.getProperty(PLACE_TYPE)));
        }
        if (entity.getProperty(RATING) != null) {
            step.setRating(ratingDtoMapper.map((Rating) entity.getProperty(RATING)));
        }
        if (entity.getProperty(UPDATED_AT) != null) {
            step.setUpdatedAt(String.valueOf((Date) entity.getProperty(UPDATED_AT)));
        }
        if (entity.getProperty(WAY_TYPE) != null) {
            step.setWayType(String.valueOf(entity.getProperty(WAY_TYPE)));
        }
        //
        step.setPlaceResultId(String.valueOf(entity.getProperty(PLACE_RESULT_ID_FOR_STEP)));
        LOGGER.info("--> StepDtoMapper.map - END");
        return step;
    }
}
