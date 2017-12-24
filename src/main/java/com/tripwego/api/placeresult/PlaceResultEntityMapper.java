package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.GeoPtEntityMapper;
import com.tripwego.api.utils.I18nUtils;
import com.tripwego.dto.placeresult.PlaceResultDto;

import java.util.Date;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 19/02/17.
 */
class PlaceResultEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultEntityMapper.class.getName());
    public static final int INIT_COUNTER = 0;
    public static final int DEFAULT_ORDER = 9999;

    private final GeoPtEntityMapper geoPtEntityMapper;

    public PlaceResultEntityMapper(GeoPtEntityMapper geoPtEntityMapper) {
        this.geoPtEntityMapper = geoPtEntityMapper;
    }

    public Entity map(PlaceResultDto placeResult) {
        LOGGER.info("--> PlaceResultEntityMapper.map - START");
        Entity entity = new Entity(KIND_PLACE_RESULT, placeResult.getPlace_id());
        entity.setProperty(PLACE_RESULT_ID, placeResult.getPlace_id());
        entity.setProperty(NAME, placeResult.getName());
        if (placeResult.getCountry() != null) {
            entity.setProperty(COUNTRY_CODE, placeResult.getCountry().getCode());
            entity.setProperty(COUNTRY_NAME_EN, I18nUtils.findDefaultCountryName(placeResult.getCountry().getCode()));
        }
        //
        entity.setProperty(CERTIFIED, false);
        entity.setProperty(COUNTER, INIT_COUNTER);
        entity.setProperty(CREATED_AT, new Date());
        if (placeResult.getFormatted_address() != null) {
            entity.setProperty(ADDRESS, new PostalAddress(placeResult.getFormatted_address()));
        }
        entity.setProperty(PHONE_NUMBER, placeResult.getFormatted_phone_number());
        entity.setProperty(HTML_ATTRIBUTIONS, placeResult.getHtml_attributions());
        entity.setProperty(ICON, placeResult.getIcon());
        entity.setProperty(INTERNATIONAL_PHONE_NUMBER, placeResult.getInternational_phone_number());
        entity.setProperty(PERMANENTLY_CLOSED, false);
        entity.setProperty(IS_EVALUATED, false);
        entity.setProperty(PHOTOS, placeResult.getPhotos());
        if (placeResult.getPlaceType() != null) {
            entity.setProperty(PLACE_TYPE, new Category(placeResult.getPlaceType()));
        }
        entity.setProperty(PRICE_LEVEL, placeResult.getPrice_level());
        entity.setProperty(ORDER, DEFAULT_ORDER);
        entity.setProperty(PROVIDER, placeResult.getProvider().name());
        entity.setProperty(RATING, new Rating(placeResult.getRating()));
        entity.setProperty(TYPES, placeResult.getTypes());
        entity.setProperty(STEP_CATEGORIES, placeResult.getStepCategories());
        entity.setProperty(STEP_TYPES, placeResult.getStepTypes());
        entity.setProperty(UPDATED_AT, new Date());
        if (placeResult.getUrl() != null) {
            entity.setProperty(URL_SITE, new Link(placeResult.getUrl()));
        }
        entity.setProperty(VICINITY, placeResult.getVicinity());
        if (placeResult.getWebsite() != null) {
            entity.setProperty(WEBSITE, new Link(placeResult.getWebsite()));
        }
        if (placeResult.getGeometry().getLocation() != null) {
            entity.setProperty(GEO_PT, geoPtEntityMapper.map(placeResult.getGeometry().getLocation()));
        }
        if (placeResult.getGeometry().getViewport() != null) {
            final GeoPt geoPt = geoPtEntityMapper.map(placeResult.getGeometry().getViewport().getCenter());
            entity.setProperty(CENTER_PT, geoPt);
            entity.setProperty(LATITUDE, geoPt.getLatitude());
            entity.setProperty(LONGITUDE, geoPt.getLongitude());
            entity.setProperty(NORTH_EAST_PT, geoPtEntityMapper.map(placeResult.getGeometry().getViewport().getNorthEast()));
            entity.setProperty(SOUTH_WEST_PT, geoPtEntityMapper.map(placeResult.getGeometry().getViewport().getSouthWest()));
        }
        updateTranslation(entity, placeResult);
        entity.setProperty(POPULATION, placeResult.getPopulation());
        LOGGER.info("--> PlaceResultEntityMapper.map - END");
        return entity;
    }

    public void updateTranslation(Entity entity, PlaceResultDto placeResult) {
        if (placeResult.getLanguage() != null) {
            LOGGER.info("--> updateTranslation - START : " + placeResult.getLanguage());
            final Object property = entity.getProperty(EMBEDDED_TRANSLATION);
            final EmbeddedEntity embeddedEntity = property != null ? (EmbeddedEntity) property : new EmbeddedEntity();
            if (embeddedEntity.getProperty(placeResult.getLanguage()) == null) {
                embeddedEntity.setProperty(placeResult.getLanguage(), placeResult.getName());
            }
            entity.setProperty(EMBEDDED_TRANSLATION, embeddedEntity);
            LOGGER.info("--> updateTranslation - END");
        }
    }
}
