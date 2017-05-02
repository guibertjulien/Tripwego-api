package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Strings;
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
        entity.setProperty(COUNTRY_NAME, trip.getCountryName());
        entity.setProperty(DESCRIPTION, new Text(Strings.nullToEmpty(trip.getDescription())));
        entity.setProperty(DURATION, trip.getDuration());
        entity.setProperty(START_DATE, DateUtil.deserializeDate(trip.getStartDate()));
        entity.setProperty(END_DATE, DateUtil.deserializeDate(trip.getEndDate()));
        entity.setProperty(IS_PRIVATE, trip.isPrivateTrip());
        entity.setProperty(IS_PUBLISHED, trip.isPublished());
        entity.setProperty(IS_ANONYMOUS, trip.isAnonymous());
        entity.setProperty(IS_CANCELLED, trip.isCancelled());
        entity.setProperty(RATING, new Rating(trip.getRating()));
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
        //entity.setProperty(CREATED_AT, trip.getCreatedAt());
        //entity.setProperty(UPDATED_AT, trip.getUpdatedAt());
        //entity.setProperty(IS_DEFAULT, trip.getTripVersion().isDefault());
        // TODO check
        //entity.setProperty(NAME, trip.getAutoTags());
        //entity.setProperty(NAME, trip.getManualTags());
        //entity.setProperty(NAME, trip.getSteps());
        //entity.setProperty(NAME, trip.getTravelers());
        //entity.setProperty(NAME, trip.getTripVersion());
        LOGGER.info("--> TripEntityMapper.map - END");
    }
}
