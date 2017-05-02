package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.api.common.mapper.*;
import com.tripwego.dto.common.LatLngBoundsDto;
import com.tripwego.dto.placeresult.PlaceGeometryDto;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.placeresult.Provider;
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

    public PlaceResultDto map(Entity entity) {
        LOGGER.info("--> PlaceResultDto.map - START");
        final PlaceResultDto result = new PlaceResultDto();
        // ids
        result.setPlace_id(String.valueOf(entity.getProperty(PLACE_RESULT_ID)));
        result.setPlaceKey(KeyFactory.keyToString(entity.getKey()));
        // strings
        result.setName(String.valueOf(entity.getProperty(NAME)));
        result.setCountry(extractCountry(entity));
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
        // lists
        if (entity.getProperty(PHOTOS) != null) {
            result.getPhotos().addAll((ArrayList<String>) entity.getProperty(PHOTOS));
        }
        if (entity.getProperty(TYPES) != null) {
            result.getTypes().addAll((ArrayList<String>) entity.getProperty(TYPES));
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
            result.setRating(ratingDtoMapper.map((Rating) entity.getProperty(RATING)));
        }
        result.setPrice_level((long) entity.getProperty(Constants.PRICE_LEVEL));
        result.setCounter((long) entity.getProperty(Constants.COUNTER));
        // booleans
        result.setEvaluated((Boolean) entity.getProperty(IS_EVALUATED));
        result.setCertifiedByTripwego((Boolean) entity.getProperty(CERTIFIED));
        result.setPermanently_closed((Boolean) entity.getProperty(PERMANENTLY_CLOSED));
        // others
        if (entity.getProperty(PROVIDER) != null) {
            result.setProvider(Provider.valueOf((String) entity.getProperty(PROVIDER)));
        }
        updateGeometry(entity, result);
        LOGGER.info("--> PlaceResultDto.map - END");
        return result;
    }

    private CountryDto extractCountry(Entity entity) {
        return new CountryDto(String.valueOf(entity.getProperty(COUNTRY_CODE)), String.valueOf(entity.getProperty(COUNTRY_NAME)));
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
