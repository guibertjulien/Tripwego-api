package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.*;
import com.tripwego.api.utils.I18nUtils;
import com.tripwego.dto.common.LatLngBoundsDto;
import com.tripwego.dto.placeresult.PlaceGeometryDto;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.trip.CountryDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

class PlaceResultDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultDtoMapper.class.getName());

    private final PostalAddressDtoMapper postalAddressDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final LinkDtoMapper linkDtoMapper;
    private final RatingDtoMapper ratingDtoMapper;
    private final LatLngDtoMapper latLngDtoMapper;

    PlaceResultDtoMapper(PostalAddressDtoMapper postalAddressDtoMapper, CategoryDtoMapper categoryDtoMapper, LinkDtoMapper linkDtoMapper, RatingDtoMapper ratingDtoMapper, LatLngDtoMapper latLngDtoMapper) {
        this.postalAddressDtoMapper = postalAddressDtoMapper;
        this.categoryDtoMapper = categoryDtoMapper;
        this.linkDtoMapper = linkDtoMapper;
        this.ratingDtoMapper = ratingDtoMapper;
        this.latLngDtoMapper = latLngDtoMapper;
    }

    public PlaceResultDto map(Entity entity, String language) {
        LOGGER.info("--> PlaceResultDto.map - START");
        final PlaceResultDto result = new PlaceResultDto();
        // ids
        result.setPlace_id(String.valueOf(entity.getProperty(PLACE_ID)));
        result.setPlaceKey(KeyFactory.keyToString(entity.getKey()));
        // strings
        result.setName(extractNameTranslated(entity, language));
        result.setCountry(extractCountryTranslated(entity, language));
        result.setFormatted_phone_number(String.valueOf(entity.getProperty(PHONE_NUMBER)));
        result.setHtml_attributions(String.valueOf(entity.getProperty(HTML_ATTRIBUTIONS)));
        result.setIcon(String.valueOf(entity.getProperty(ICON)));
        result.setInternational_phone_number(String.valueOf(entity.getProperty(INTERNATIONAL_PHONE_NUMBER)));
        result.setVicinity(String.valueOf(entity.getProperty(VICINITY)));
        // address
        if (entity.getProperty(ADDRESS) != null) {
            result.setFormatted_address(postalAddressDtoMapper.map(entity.getProperty(ADDRESS)));
        }
        // categories
        if (entity.getProperty(PLACE_TYPE) != null) {
            result.setPlaceType(categoryDtoMapper.map(entity.getProperty(PLACE_TYPE)));
        }
        if (entity.getProperty(TYPES) != null) {
            result.getTypes().addAll((ArrayList<String>) entity.getProperty(TYPES));
        }
        if (entity.getProperty(SUGGESTION_TYPES) != null) {
            result.getSuggestionTypes().addAll((ArrayList<String>) entity.getProperty(SUGGESTION_TYPES));
        }
        // dates
        result.setCreatedAt(String.valueOf((Date) entity.getProperty(CREATED_AT)));
        result.setUpdatedAt(String.valueOf((Date) entity.getProperty(UPDATED_AT)));
        // links
        if (entity.getProperty(INFO_LINK) != null) {
            result.setUrl(linkDtoMapper.map((Link) entity.getProperty(INFO_LINK)));
        }
        if (entity.getProperty(WEBSITE) != null) {
            result.setWebsite(linkDtoMapper.map((Link) entity.getProperty(WEBSITE)));
        }
        // numbers
        if (entity.getProperty(RATING) != null) {
            result.setRating((double) entity.getProperty(RATING));
        }
        result.setPrice_level((long) entity.getProperty(PRICE_LEVEL));
        result.setCounter((long) entity.getProperty(COUNTER));
        // booleans
        result.setEvaluated((Boolean) entity.getProperty(IS_EVALUATED));
        result.setPermanently_closed((Boolean) entity.getProperty(PERMANENTLY_CLOSED));
        result.setPopulation((long) entity.getProperty(POPULATION));
        updateGeometry(entity, result);
        result.setLanguage(language);
        LOGGER.info("--> PlaceResultDto.map - END");
        return result;
    }

    // TODO optimize in Java8
    private String extractNameTranslated(Entity entity, String language) {
        LOGGER.info("--> extractNameTranslated - START : " + language);
        String nameTranslated = String.valueOf(entity.getProperty(NAME));
        final EmbeddedEntity embeddedEntity = (EmbeddedEntity) entity.getProperty(EMBEDDED_TRANSLATION);
        if (embeddedEntity != null) {
            Object propertyLanguage = embeddedEntity.getProperty(language);
            LOGGER.info("--> propertyLanguage : " + propertyLanguage);
            if (propertyLanguage != null) {
                nameTranslated = (String) propertyLanguage;
            } else {
                Object propertyDefaultLanguage = embeddedEntity.getProperty(I18nUtils.DEFAULT_LANGUAGE_CODE);
                LOGGER.info("--> propertyDefaultLanguage : " + propertyDefaultLanguage);
                if (propertyDefaultLanguage != null) {
                    nameTranslated = (String) propertyDefaultLanguage;
                }
            }
        }
        LOGGER.info("--> extractNameTranslated - END : " + nameTranslated);
        return nameTranslated;
    }

    private CountryDto extractCountryTranslated(Entity entity, String language) {
        final String code = String.valueOf(entity.getProperty(COUNTRY_CODE));
        String name = String.valueOf(entity.getProperty(COUNTRY_NAME_EN));
        if (language != null && !language.isEmpty()) {
            try {
                name = I18nUtils.findCountryName(language, code);
            } catch (Exception e) {
                LOGGER.warning("extractCountryTranslated : " + e.getMessage());
            }
        }
        return new CountryDto(code, name);
    }

    private void updateGeometry(Entity entity, PlaceResultDto result) {
        final PlaceGeometryDto geometry = new PlaceGeometryDto();
        geometry.setLocation(latLngDtoMapper.map((GeoPt) entity.getProperty(GEO_PT)));
        final LatLngBoundsDto viewport = new LatLngBoundsDto();
        viewport.setCenter(latLngDtoMapper.map((GeoPt) entity.getProperty(CENTER_PT)));
        viewport.setNorthEast(latLngDtoMapper.map((GeoPt) entity.getProperty(NORTH_EAST_PT)));
        viewport.setSouthWest(latLngDtoMapper.map((GeoPt) entity.getProperty(SOUTH_WEST_PT)));
        geometry.setViewport(viewport);
        result.setGeometry(geometry);
    }
}
