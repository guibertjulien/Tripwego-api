package com.tripwego.api.trip;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.common.mapper.CategoryDtoMapper;
import com.tripwego.api.common.mapper.LinkDtoMapper;
import com.tripwego.api.common.mapper.RatingDtoMapper;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripProvider;
import com.tripwego.dto.trip.TripVersion;
import com.tripwego.dto.user.MyUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

public class TripDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(TripDtoMapper.class.getName());

    private final CategoryDtoMapper categoryDtoMapper;
    private final LinkDtoMapper linkDtoMapper;
    private final RatingDtoMapper ratingDtoMapper;

    public TripDtoMapper(CategoryDtoMapper categoryDtoMapper, LinkDtoMapper linkDtoMapper, RatingDtoMapper ratingDtoMapper) {
        this.categoryDtoMapper = categoryDtoMapper;
        this.linkDtoMapper = linkDtoMapper;
        this.ratingDtoMapper = ratingDtoMapper;
    }

    public Trip map(Entity entity, Optional<MyUser> user) {
        LOGGER.info("--> TripMapper.map - START");
        final Trip trip = new Trip();
        trip.setId(KeyFactory.keyToString(entity.getKey()));
        if (entity.getProperty(PARENT_TRIP_ID) != null) {
            trip.setParentTripId(String.valueOf(entity.getProperty(PARENT_TRIP_ID)));
        }
        trip.setName(String.valueOf(entity.getProperty(NAME)));
        if (entity.getProperty(DESCRIPTION) != null) {
            trip.setDescription(((Text) entity.getProperty(DESCRIPTION)).getValue());
        }
        trip.setCategory(categoryDtoMapper.map(entity.getProperty(CATEGORY)));
        // TODO use ?
        trip.setCountryCode(String.valueOf(entity.getProperty(COUNTRY_CODE)));
        trip.setCountryName(String.valueOf(entity.getProperty(COUNTRY_NAME)));
        //
        if (entity.getProperty(CREATED_AT) != null) {
            trip.setCreatedAt(String.valueOf(entity.getProperty(CREATED_AT)));
        }
        if (entity.getProperty(UPDATED_AT) != null) {
            trip.setUpdatedAt(String.valueOf(entity.getProperty(UPDATED_AT)));
        }
        if (entity.getProperty(DURATION) != null) {
            trip.setDuration((Long) entity.getProperty(DURATION));
        }
        if (entity.getProperty(RATING) != null) {
            trip.setRating(ratingDtoMapper.map(entity.getProperty(RATING)));
        }
        if (entity.getProperty(START_DATE) != null) {
            trip.setStartDate(DateUtil.serializeDate((Date) entity.getProperty(START_DATE)));
        }
        if (entity.getProperty(END_DATE) != null) {
            trip.setEndDate(DateUtil.serializeDate((Date) entity.getProperty(END_DATE)));
        }
        if (entity.getProperty(URL_STATIC_MAP) != null) {
            trip.setUrlStaticMap(linkDtoMapper.map(entity.getProperty(URL_STATIC_MAP)));
        }
        if (user.isPresent()) {
            trip.setUser(user.get());
        }
        // TRIP VERSION
        final EmbeddedEntity entityVersion = (EmbeddedEntity) entity.getProperty(EMBEDDED_VERSION);
        final TripVersion tripVersion = new TripVersion();
        if (entityVersion != null) {
            tripVersion.setDefault((Boolean) entity.getProperty(IS_DEFAULT));
            tripVersion.setCreatedAt(DateUtil.serializeDate((Date) entityVersion.getProperty(VERSION_CREATED_AT)));
            tripVersion.setNumber((Long) entityVersion.getProperty(VERSION_NUMBER));
            tripVersion.setParentTripId(KeyFactory.keyToString(entity.getKey()));
            //tripVersion.setUserUpdater((User) entity.getProperty(VERSION_USER));
            trip.setTripVersion(tripVersion);
        }
        // TRIP PROVIDER
        final EmbeddedEntity entityProvider = (EmbeddedEntity) entity.getProperty(EMBEDDED_PROVIDER);
        final TripProvider tripProvider = new TripProvider();
        if (entityProvider != null) {
            tripProvider.setEmail(String.valueOf(entityProvider.getProperty(EMAIL)));
            tripProvider.setName(String.valueOf(entityProvider.getProperty(NAME)));
            tripProvider.setType(String.valueOf(entityProvider.getProperty(TYPE)));
            tripProvider.setUrl(linkDtoMapper.map(entityProvider.getProperty(URL_SITE)));
            trip.setTripProvider(tripProvider);
        }
        if (entity.getProperty(IS_PRIVATE) != null) {
            trip.setPrivateTrip((Boolean) entity.getProperty(IS_PRIVATE));
        }
        if (entity.getProperty(IS_PUBLISHED) != null) {
            trip.setPublished((Boolean) entity.getProperty(IS_PUBLISHED));
        }
        if (entity.getProperty(IS_ANONYMOUS) != null) {
            trip.setAnonymous((Boolean) entity.getProperty(IS_ANONYMOUS));
        }
        if (entity.getProperty(IS_CANCELLED) != null) {
            trip.setCancelled((Boolean) entity.getProperty(IS_CANCELLED));
        }
        if (entity.getProperty(IS_NO_SPECIFIC_DATES) != null) {
            trip.setNoSpecificDates((Boolean) entity.getProperty(IS_NO_SPECIFIC_DATES));
        }
        if (entity.getProperty(URL_SITE) != null) {
            trip.setUrlSite(linkDtoMapper.map(entity.getProperty(URL_SITE)));
        }
        if (entity.getProperty(URL_ALBUM_PHOTO) != null) {
            trip.setUrlPhotoAlbum(linkDtoMapper.map(entity.getProperty(URL_ALBUM_PHOTO)));
        }
        if (entity.getProperty(URL_PHOTO) != null) {
            trip.setUrlPhoto(linkDtoMapper.map(entity.getProperty(URL_PHOTO)));
        }
        //
        trip.setPlaceResultId(String.valueOf(entity.getProperty(PLACE_RESULT_ID)));
        // TODO
        //trip.setAutoTags(entity.getProperty(NAME));
        //trip.setManualTags(entity.getProperty(NAME));
        //trip.setTravelers(entity.getProperty(NAME));
        //trip.setUrlStaticMap(entity.getProperty(NAME));

        if (entity.getProperty(TAGS) != null) {
            Object property = entity.getProperty(TAGS);
            trip.getTags().addAll((ArrayList<String>) property);
        }

        LOGGER.info("--> TripMapper.map - END");
        return trip;
    }
}
