package com.tripwego.api.trip;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.DateUtil;
import com.tripwego.api.common.mapper.CategoryDtoMapper;
import com.tripwego.api.common.mapper.LinkDtoMapper;
import com.tripwego.api.common.mapper.RatingDtoMapper;
import com.tripwego.dto.common.Seo;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripProvider;
import com.tripwego.dto.trip.TripVersion;
import com.tripwego.dto.user.MyUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
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
        trip.setCountryCode(String.valueOf(entity.getProperty(COUNTRY_CODE)));
        trip.setCountryName(String.valueOf(entity.getProperty(COUNTRY_NAME_EN)));
        //
        if (entity.getProperty(CREATED_AT) != null) {
            trip.setCreatedAt(DateUtil.serializeDate((Date) entity.getProperty(CREATED_AT)));
        }
        if (entity.getProperty(UPDATED_AT) != null) {
            trip.setUpdatedAt(DateUtil.serializeDate((Date) entity.getProperty(UPDATED_AT)));
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
        trip.setTripAdminStatus(String.valueOf(entity.getProperty(TRIP_ADMIN_STATUS)));
        trip.setTripUserStatus(String.valueOf(entity.getProperty(TRIP_USER_STATUS)));
        trip.setTripPlanStatus(String.valueOf(entity.getProperty(TRIP_PLAN_STATUS)));
        trip.setTripVisibility(String.valueOf(entity.getProperty(TRIP_VISIBILITY)));
        trip.setTripCertificate(String.valueOf(entity.getProperty(TRIP_CERTIFICATE)));
        trip.setMapStyle(String.valueOf(entity.getProperty(MAP_STYLE)));
        trip.setWayTypeDefault(String.valueOf(entity.getProperty(WAY_TYPE_DEFAULT)));
        //
        trip.setPlaceResultId(String.valueOf(entity.getProperty(PLACE_RESULT_ID)));
        trip.setLanguage(String.valueOf(entity.getProperty(LANGUAGE)));
        if (entity.getProperty(TAGS) != null) {
            Object property = entity.getProperty(TAGS);
            trip.getTags().addAll((ArrayList<String>) property);
        }
        // embedded
        updateVersion(entity, trip);
        updateProvider(entity, trip);
        updateSeo(entity, trip);
        LOGGER.info("--> TripMapper.map - END");
        return trip;
    }

    private void updateVersion(Entity entity, Trip trip) {
        final EmbeddedEntity embeddedEntity = (EmbeddedEntity) entity.getProperty(EMBEDDED_VERSION);
        final TripVersion tripVersion = new TripVersion();
        if (embeddedEntity != null) {
            tripVersion.setDefault((Boolean) entity.getProperty(IS_DEFAULT));
            tripVersion.setCreatedAt(DateUtil.serializeDate((Date) embeddedEntity.getProperty(VERSION_CREATED_AT)));
            tripVersion.setNumber((Long) embeddedEntity.getProperty(VERSION_NUMBER));
            tripVersion.setParentTripId(KeyFactory.keyToString(entity.getKey()));
            //tripVersion.setUserUpdater((User) entity.getProperty(VERSION_USER));
            trip.setTripVersion(tripVersion);
        }
    }

    private void updateProvider(Entity entity, Trip trip) {
        final EmbeddedEntity embeddedEntity = (EmbeddedEntity) entity.getProperty(EMBEDDED_PROVIDER);
        final TripProvider tripProvider = new TripProvider();
        if (embeddedEntity != null) {
            tripProvider.setEmail(String.valueOf(embeddedEntity.getProperty(EMAIL)));
            tripProvider.setName(String.valueOf(embeddedEntity.getProperty(NAME)));
            tripProvider.setType(String.valueOf(embeddedEntity.getProperty(TYPE)));
            tripProvider.setUrl(linkDtoMapper.map(embeddedEntity.getProperty(URL_SITE)));
            trip.setTripProvider(tripProvider);
        }
    }

    private void updateSeo(Entity entity, Trip trip) {
        final EmbeddedEntity embeddedEntity = (EmbeddedEntity) entity.getProperty(EMBEDDED_SEO);
        final Seo seo = new Seo();
        if (embeddedEntity != null) {
            seo.setTitle(String.valueOf(embeddedEntity.getProperty(TITLE)));
            seo.setDescription(String.valueOf(embeddedEntity.getProperty(DESCRIPTION)));
            seo.setKeywords(String.valueOf(embeddedEntity.getProperty(KEYWORDS)));
            seo.setUrlParameterName(String.valueOf(embeddedEntity.getProperty(URL_PARAMETER_NAME)));
            trip.setSeo(seo);
        }
    }
}
