package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.Rating;

/**
 * Created by JG on 17/11/16.
 */
public class RatingMapper {

    public com.tripwego.dto.datastore.Rating map(Rating rating) {
        final com.tripwego.dto.datastore.Rating result = new com.tripwego.dto.datastore.Rating();
        result.setRating(rating.getRating());
        return result;
    }
}
