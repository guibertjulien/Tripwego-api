package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.tripwego.api.utils.I18nUtils;
import com.tripwego.api.utils.Strings;
import com.tripwego.dto.common.Seo;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;

import java.util.Optional;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;
import static com.tripwego.api.trip.status.TripAdminStatus.CREATED;

public class TripEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(TripEntityMapper.class.getName());

    public void map(final Entity entity, Trip trip, Optional<MyUser> user) {
        LOGGER.info("--> TripEntityMapper.map - START");
        entity.setProperty(PARENT_TRIP_ID, trip.getParentTripId());
        entity.setProperty(NAME, trip.getName());
        entity.setProperty(CONTINENT, trip.getContinent());
        entity.setProperty(COUNTRY_CODE, trip.getCountryCode());
        entity.setProperty(COUNTRY_NAME_EN, I18nUtils.findDefaultCountryName(trip.getCountryCode()));
        entity.setProperty(DESCRIPTION, new Text(Strings.nullToEmpty(trip.getDescription())));
        entity.setProperty(DURATION, trip.getDuration());
        entity.setProperty(START_DATE, DateUtil.deserializeDate(trip.getStartDate()));
        entity.setProperty(END_DATE, DateUtil.deserializeDate(trip.getEndDate()));
        entity.setProperty(IS_CANCELLED_BY_USER, trip.isCancelled());
        entity.setProperty(IS_NO_SPECIFIC_DATES, trip.isNoSpecificDates());
        entity.setProperty(RATING, new Rating(trip.getRating()));
        //
        entity.setProperty(TRIP_USER_STATUS, trip.getTripUserStatus());
        entity.setProperty(TRIP_PLAN_STATUS, trip.getTripPlanStatus());
        entity.setProperty(TRIP_VISIBILITY, trip.getTripVisibility());
        entity.setProperty(TRIP_CERTIFICATE, trip.getTripCertificate());
        entity.setProperty(MAP_STYLE, trip.getMapStyle());
        entity.setProperty(WAY_TYPE_DEFAULT, trip.getWayTypeDefault());
        //
        if (trip.getUrlSite() != null) {
            entity.setProperty(URL_SITE, new Link(trip.getUrlSite()));
        }
        if (trip.getUrlPhotoAlbum() != null) {
            entity.setProperty(URL_ALBUM_PHOTO, new Link(trip.getUrlPhotoAlbum()));
        }
        if (trip.getUrlStaticMap() != null) {
            entity.setProperty(URL_STATIC_MAP, new Link(trip.getUrlStaticMap()));
        }
        if (trip.getUrlPhoto() != null) {
            if (!trip.getUrlPhoto().contains(PHOTO_SERVICE_ERROR_FRAGMENT)) {
                entity.setProperty(URL_PHOTO, new Link(trip.getUrlPhoto()));
            }
        }
        if (user.isPresent()) {
            entity.setProperty(USER_ID, user.get().getUserId());
        } else {
            entity.setProperty(USER_ID, null);
        }
        entity.setProperty(TAGS, trip.getTags());
        entity.setProperty(LANGUAGE, trip.getLanguage());
        updateSeo(trip, entity);
        LOGGER.info("--> TripEntityMapper.map - END");
    }

    private void updateSeo(Trip trip, Entity entity) {
        LOGGER.info("--> updateSeo - START");
        final Seo seo = trip.getSeo();
        EmbeddedEntity embeddedEntity = (EmbeddedEntity) entity.getProperty(EMBEDDED_SEO);
        if (embeddedEntity == null) {
            embeddedEntity = new EmbeddedEntity();
        }
        embeddedEntity.setProperty(TITLE, seo.getTitle());
        embeddedEntity.setProperty(DESCRIPTION, seo.getDescription());
        embeddedEntity.setProperty(KEYWORDS, seo.getKeywords());
        entity.setProperty(EMBEDDED_SEO, embeddedEntity);
        // ONLY IF CREATED, NOT ALTERABLE
        if (CREATED.name().equals(trip.getTripAdminStatus())) {
            embeddedEntity.setProperty(URL_PARAMETER_NAME, seo.getUrlParameterName());
        }
        LOGGER.info("--> updateSeo - END");
    }

}
