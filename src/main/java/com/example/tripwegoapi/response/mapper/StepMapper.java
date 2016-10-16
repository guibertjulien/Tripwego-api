package com.example.tripwegoapi.response.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.Step;
import com.google.appengine.api.datastore.*;

import java.util.Date;


public class StepMapper {

    public Step map(Entity entity) {
        Step step = new Step();
        step.setId(KeyFactory.keyToString(entity.getKey()));
        step.setParentId(String.valueOf(entity.getParent().getId()));
        if (entity.getProperty(Constants.PARENT_STEP_ID) != null) {
            step.setParentStepId(String.valueOf(entity.getProperty(Constants.PARENT_STEP_ID)));
        }
        step.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        if (entity.getProperty(Constants.ADDRESS) != null) {
            step.setAddress((PostalAddress) entity.getProperty(Constants.ADDRESS));
        }
        if (entity.getProperty(Constants.AVOID_HIGHWAYS) != null) {
            step.setAvoidHighways((Boolean) entity.getProperty(Constants.AVOID_HIGHWAYS));
        }
        if (entity.getProperty(Constants.AVOID_TOLLS) != null) {
            step.setAvoidTolls((Boolean) entity.getProperty(Constants.AVOID_TOLLS));
        }
        if (entity.getProperty(Constants.CATEGORY) != null) {
            step.setCategory((Category) entity.getProperty(Constants.CATEGORY));
        }
        if (entity.getProperty(Constants.CREATED_AT) != null) {
            step.setCreatedAt((Date) entity.getProperty(Constants.CREATED_AT));
        }
        if (entity.getProperty(Constants.DAY_IN) != null) {
            step.setDayIn((Long) entity.getProperty(Constants.DAY_IN));
        }
        if (entity.getProperty(Constants.DAY_OUT) != null) {
            step.setDayOut((Long) entity.getProperty(Constants.DAY_OUT));
        }
        if (entity.getProperty(Constants.DESCRIPTION) != null) {
            step.setDescription(String.valueOf(entity.getProperty(Constants.DESCRIPTION)));
        }
        if (entity.getProperty(Constants.GEO_PT) != null) {
            step.setGeoPt((GeoPt) entity.getProperty(Constants.GEO_PT));
        }
        if (entity.getProperty(Constants.INDEX_ON_ROAD_MAP) != null) {
            step.setIndexOnRoadMap((Long) entity.getProperty(Constants.INDEX_ON_ROAD_MAP));
        }
        if (entity.getProperty(Constants.INFO_LINK) != null) {
            step.setInfoLink((Link) entity.getProperty(Constants.INFO_LINK));
        }
        if (entity.getProperty(Constants.URL_PHOTO) != null) {
            step.setUrlPhoto((Link) entity.getProperty(Constants.URL_PHOTO));
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
            step.setRating((Rating) entity.getProperty(Constants.RATING));
        }
        if (entity.getProperty(Constants.UPDATED_AT) != null) {
            step.setUpdatedAt((Date) entity.getProperty(Constants.UPDATED_AT));
        }
        if (entity.getProperty(Constants.WAY_TYPE) != null) {
            step.setWayType(String.valueOf(entity.getProperty(Constants.WAY_TYPE)));
        }
        return step;
    }
}
