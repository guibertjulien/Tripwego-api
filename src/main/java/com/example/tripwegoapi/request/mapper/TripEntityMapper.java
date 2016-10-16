package com.example.tripwegoapi.request.mapper;

import com.example.tripwegoapi.dto.MyUser;
import com.example.tripwegoapi.dto.Trip;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.repackaged.com.google.common.base.Optional;

import static com.example.tripwegoapi.Constants.*;

public class TripEntityMapper {

    public void map(final Entity entity, Trip trip, Optional<MyUser> user) {
        entity.setProperty(PARENT_TRIP_ID, trip.getParentTripId());
        entity.setProperty(NAME, trip.getName());
        entity.setProperty(CATEGORY, trip.getCategory());
        entity.setProperty(COUNTRY_CODE, trip.getCountryCode());
        entity.setProperty(COUNTRY_NAME, trip.getCountryName());
        //entity.setProperty(CREATED_AT, trip.getCreatedAt());
        entity.setProperty(DESCRIPTION, trip.getDescription());
        entity.setProperty(DURATION, trip.getDuration());
        entity.setProperty(END_DATE, trip.getEndDate());
        entity.setProperty(RATING, trip.getRating());
        entity.setProperty(START_DATE, trip.getStartDate());
        entity.setProperty(IS_PRIVATE, trip.isPrivateTrip());
        entity.setProperty(IS_PUBLISHED, trip.isPublished());
        entity.setProperty(IS_ANONYMOUS, trip.isAnonymous());
        entity.setProperty(IS_CANCELLED, trip.isCancelled());
        entity.setProperty(URL_SITE, trip.getUrlSite());
        entity.setProperty(URL_ALBUM_PHOTO, trip.getUrlPhotoAlbum());
        //entity.setProperty(UPDATED_AT, trip.getUpdatedAt());
        if (trip.getUrlStaticMap() != null) {
            entity.setProperty(URL_STATIC_MAP, trip.getUrlStaticMap());
        }
        entity.setProperty(URL_PHOTO, trip.getUrlPhoto());
        if (user.isPresent()) {
            entity.setProperty(USER_ID, user.get().getUserId());
        } else {
            entity.setProperty(USER_ID, null);
        }
        //entity.setProperty(IS_DEFAULT, trip.getTripVersion().isDefault());
        // TODO check
        //entity.setProperty(NAME, trip.getAutoTags());
        //entity.setProperty(NAME, trip.getManualTags());
        //entity.setProperty(NAME, trip.getSteps());
        //entity.setProperty(NAME, trip.getTravelers());
        //entity.setProperty(NAME, trip.getTripVersion());
    }
}
