package com.tripwego.api.response.mapper.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.Constants;
import com.tripwego.api.response.mapper.datastore.CategoryMapper;
import com.tripwego.api.response.mapper.datastore.LinkMapper;
import com.tripwego.api.response.mapper.datastore.RatingMapper;
import com.tripwego.dto.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class TripMapper {

    private static final Logger LOGGER = Logger.getLogger(TripMapper.class.getName());

    private final CategoryMapper categoryMapper;
    private final LinkMapper linkMapper;
    private final RatingMapper ratingMapper;

    public TripMapper(CategoryMapper categoryMapper, LinkMapper linkMapper, RatingMapper ratingMapper) {
        this.categoryMapper = categoryMapper;
        this.linkMapper = linkMapper;
        this.ratingMapper = ratingMapper;
    }

    public Trip map(Entity entity, Optional<List<Step>> steps, Optional<MyUser> user) {
        LOGGER.info("--> TripMapper.map - START");
        final Trip trip = new Trip();
        trip.setId(KeyFactory.keyToString(entity.getKey()));
        if (entity.getProperty(Constants.PARENT_TRIP_ID) != null) {
            trip.setParentTripId(String.valueOf(entity.getProperty(Constants.PARENT_TRIP_ID)));
        }
        trip.setName(String.valueOf(entity.getProperty(Constants.NAME)));
        trip.setCategory(categoryMapper.map((Category) entity.getProperty(Constants.CATEGORY)));
        trip.setCountryCode(String.valueOf(entity.getProperty(Constants.COUNTRY_CODE)));
        trip.setCountryName(String.valueOf(entity.getProperty(Constants.COUNTRY_NAME)));
        trip.setDescription(String.valueOf(entity.getProperty(Constants.DESCRIPTION)));
        if (entity.getProperty(Constants.CREATED_AT) != null) {
            trip.setCreatedAt((Date) entity.getProperty(Constants.CREATED_AT));
        }
        if (entity.getProperty(Constants.UPDATED_AT) != null) {
            trip.setUpdatedAt((Date) entity.getProperty(Constants.UPDATED_AT));
        }
        if (entity.getProperty(Constants.DURATION) != null) {
            trip.setDuration((Integer) entity.getProperty(Constants.DURATION));
        }
        if (entity.getProperty(Constants.RATING) != null) {
            trip.setRating(ratingMapper.map((Rating) entity.getProperty(Constants.RATING)));
        }
        if (entity.getProperty(Constants.START_DATE) != null) {
            trip.setStartDate((Date) entity.getProperty(Constants.START_DATE));
        }
        if (entity.getProperty(Constants.END_DATE) != null) {
            trip.setEndDate((Date) entity.getProperty(Constants.END_DATE));
        }
        if (entity.getProperty(Constants.URL_STATIC_MAP) != null) {
            trip.setUrlStaticMap(linkMapper.map((Link) entity.getProperty(Constants.URL_STATIC_MAP)));
        }
        if (user.isPresent()) {
            trip.setUser(user.get());
        }
        // TRIP VERSION
        final EmbeddedEntity entityVersion = (EmbeddedEntity) entity.getProperty(Constants.EMBEDDED_VERSION);
        final TripVersion tripVersion = new TripVersion();
        if (entityVersion != null) {
            tripVersion.setDefault((Boolean) entity.getProperty(Constants.IS_DEFAULT));
            tripVersion.setCreatedAt((Date) entityVersion.getProperty(Constants.VERSION_CREATED_AT));
            tripVersion.setNumber((Integer) entityVersion.getProperty(Constants.VERSION_NUMBER));
            tripVersion.setParentTripId(KeyFactory.keyToString(entity.getKey()));
            //tripVersion.setUserUpdater((User) entity.getProperty(VERSION_USER));
            trip.setTripVersion(tripVersion);
        }
        // TRIP PROVIDER
        final EmbeddedEntity entityProvider = (EmbeddedEntity) entity.getProperty(Constants.EMBEDDED_PROVIDER);
        final TripProvider tripProvider = new TripProvider();
        if (entityProvider != null) {
            tripProvider.setEmail(String.valueOf(entityProvider.getProperty(Constants.EMAIL)));
            tripProvider.setName(String.valueOf(entityProvider.getProperty(Constants.NAME)));
            tripProvider.setType(String.valueOf(entityProvider.getProperty(Constants.TYPE)));
            tripProvider.setUrl(linkMapper.map((Link) entityProvider.getProperty(Constants.URL_SITE)));
            trip.setTripProvider(tripProvider);
        }
        // STEPS
        if (steps.isPresent()) {
            trip.getSteps().addAll(steps.get());
        }
        if (entity.getProperty(Constants.IS_PRIVATE) != null) {
            trip.setPrivateTrip((Boolean) entity.getProperty(Constants.IS_PRIVATE));
        }
        if (entity.getProperty(Constants.IS_PUBLISHED) != null) {
            trip.setPublished((Boolean) entity.getProperty(Constants.IS_PUBLISHED));
        }
        if (entity.getProperty(Constants.IS_ANONYMOUS) != null) {
            trip.setAnonymous((Boolean) entity.getProperty(Constants.IS_ANONYMOUS));
        }
        if (entity.getProperty(Constants.IS_CANCELLED) != null) {
            trip.setCancelled((Boolean) entity.getProperty(Constants.IS_CANCELLED));
        }
        if (entity.getProperty(Constants.URL_SITE) != null) {
            trip.setUrlSite(linkMapper.map((Link) entity.getProperty(Constants.URL_SITE)));
        }
        if (entity.getProperty(Constants.URL_ALBUM_PHOTO) != null) {
            trip.setUrlPhotoAlbum(linkMapper.map((Link) entity.getProperty(Constants.URL_ALBUM_PHOTO)));
        }
        if (entity.getProperty(Constants.URL_PHOTO) != null) {
            trip.setUrlPhoto(linkMapper.map((Link) entity.getProperty(Constants.URL_PHOTO)));
        }
        // TODO
        //trip.setAutoTags(entity.getProperty(NAME));
        //trip.setManualTags(entity.getProperty(NAME));
        //trip.setTravelers(entity.getProperty(NAME));
        //trip.setUrlStaticMap(entity.getProperty(NAME));
        LOGGER.info("--> TripMapper.map - END");
        return trip;
    }
}
