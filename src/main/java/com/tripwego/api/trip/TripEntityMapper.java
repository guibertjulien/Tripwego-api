package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.tripwego.api.I18nUtils;
import com.tripwego.dto.common.Seo;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;

import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

public class TripEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(TripEntityMapper.class.getName());

    public void map(final Entity entity, Trip trip, Optional<MyUser> user) {
        LOGGER.info("--> TripEntityMapper.map - START");
        entity.setProperty(PARENT_TRIP_ID, trip.getParentTripId());
        entity.setProperty(NAME, trip.getName());
        entity.setProperty(COUNTRY_CODE, trip.getCountryCode());
        entity.setProperty(COUNTRY_NAME_EN, I18nUtils.findDefaultCountryName(trip.getCountryCode()));
        entity.setProperty(DESCRIPTION, new Text(Strings.nullToEmpty(trip.getDescription())));
        entity.setProperty(DURATION, trip.getDuration());
        entity.setProperty(START_DATE, DateUtil.deserializeDate(trip.getStartDate()));
        entity.setProperty(END_DATE, DateUtil.deserializeDate(trip.getEndDate()));
        entity.setProperty(IS_CANCELLED, trip.isCancelled());
        entity.setProperty(IS_NO_SPECIFIC_DATES, trip.isNoSpecificDates());
        entity.setProperty(RATING, new Rating(trip.getRating()));
        //
        entity.setProperty(TRIP_ADMIN_STATUS, trip.getTripAdminStatus());
        entity.setProperty(TRIP_USER_STATUS, trip.getTripUserStatus());
        entity.setProperty(TRIP_PLAN_STATUS, trip.getTripPlanStatus());
        entity.setProperty(TRIP_VISIBILITY, trip.getTripVisibility());
        entity.setProperty(TRIP_CERTIFICATE, trip.getTripCertificate());
        entity.setProperty(MAP_STYLE, trip.getMapStyle());
        //
        if (trip.getCategory() != null) {
            entity.setProperty(CATEGORY, new Category(trip.getCategory()));
        }
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
            entity.setProperty(URL_PHOTO, new Link(trip.getUrlPhoto()));
        }
        if (user.isPresent()) {
            entity.setProperty(USER_ID, user.get().getUserId());
        } else {
            entity.setProperty(USER_ID, null);
        }
        entity.setProperty(TAGS, trip.getTags());
        entity.setProperty(LANGUAGE, trip.getLanguage());
        // embedded
        updateSeo(trip, entity);
        LOGGER.info("--> TripEntityMapper.map - END");
    }

    private void updateSeo(Trip trip, Entity entity) {
        LOGGER.info("--> updateSeo - START");
        final EmbeddedEntity embeddedEntity = new EmbeddedEntity();
        final Seo seo = trip.getSeo();
        embeddedEntity.setProperty(TITLE, seo.getTitle());
        embeddedEntity.setProperty(DESCRIPTION, seo.getDescription());
        embeddedEntity.setProperty(KEYWORDS, seo.getKeywords());
        embeddedEntity.setProperty(URL_SITE, seo.getUrl());
        entity.setProperty(EMBEDDED_SEO, embeddedEntity);
        LOGGER.info("--> updateSeo - END");
    }

}
