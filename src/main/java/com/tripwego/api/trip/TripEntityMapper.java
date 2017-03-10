package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.tripwego.api.Constants;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;

import java.util.logging.Logger;

public class TripEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(TripEntityMapper.class.getName());

    public void map(final Entity entity, Trip trip, Optional<MyUser> user) {
        LOGGER.info("--> TripEntityMapper.map - START");
        entity.setProperty(Constants.PARENT_TRIP_ID, trip.getParentTripId());
        entity.setProperty(Constants.NAME, trip.getName());
        entity.setProperty(Constants.COUNTRY_CODE, trip.getCountryCode());
        entity.setProperty(Constants.COUNTRY_NAME, trip.getCountryName());
        entity.setProperty(Constants.DESCRIPTION, new Text(Strings.nullToEmpty(trip.getDescription())));
        entity.setProperty(Constants.DURATION, trip.getDuration());
        entity.setProperty(Constants.START_DATE, DateUtil.deserializeDate(trip.getStartDate()));
        entity.setProperty(Constants.END_DATE, DateUtil.deserializeDate(trip.getEndDate()));
        entity.setProperty(Constants.IS_PRIVATE, trip.isPrivateTrip());
        entity.setProperty(Constants.IS_PUBLISHED, trip.isPublished());
        entity.setProperty(Constants.IS_ANONYMOUS, trip.isAnonymous());
        entity.setProperty(Constants.IS_CANCELLED, trip.isCancelled());
        entity.setProperty(Constants.RATING, new Rating(trip.getRating()));
        if (trip.getCategory() != null) {
            entity.setProperty(Constants.CATEGORY, new Category(trip.getCategory()));
        }
        if (trip.getUrlSite() != null) {
            entity.setProperty(Constants.URL_SITE, new Link(trip.getUrlSite()));
        }
        if (trip.getUrlPhotoAlbum() != null) {
            entity.setProperty(Constants.URL_ALBUM_PHOTO, new Link(trip.getUrlPhotoAlbum()));
        }
        if (trip.getUrlStaticMap() != null) {
            entity.setProperty(Constants.URL_STATIC_MAP, new Link(trip.getUrlStaticMap()));
        }
        if (trip.getUrlPhoto() != null) {
            entity.setProperty(Constants.URL_PHOTO, new Link(trip.getUrlPhoto()));
        }
        if (user.isPresent()) {
            entity.setProperty(Constants.USER_ID, user.get().getUserId());
        } else {
            entity.setProperty(Constants.USER_ID, null);
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
