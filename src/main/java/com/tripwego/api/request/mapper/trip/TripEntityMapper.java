package com.tripwego.api.request.mapper.trip;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.Constants;
import com.tripwego.domain.MyUser;
import com.tripwego.domain.Trip;

import java.util.logging.Logger;

public class TripEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(TripEntityMapper.class.getName());

    public void map(final Entity entity, Trip trip, Optional<MyUser> user) {

        LOGGER.info("--> TripEntityMapper.map - START");
        entity.setProperty(Constants.PARENT_TRIP_ID, trip.getParentTripId());
        entity.setProperty(Constants.NAME, trip.getName());
        entity.setProperty(Constants.COUNTRY_CODE, trip.getCountryCode());
        entity.setProperty(Constants.COUNTRY_NAME, trip.getCountryName());
        entity.setProperty(Constants.DESCRIPTION, trip.getDescription());
        entity.setProperty(Constants.DURATION, trip.getDuration());
        entity.setProperty(Constants.START_DATE, trip.getStartDate());
        entity.setProperty(Constants.END_DATE, trip.getEndDate());
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
