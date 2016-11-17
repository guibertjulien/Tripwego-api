package com.tripwego.api.request.mapper.datastore;

import com.google.appengine.api.datastore.Rating;

/**
 * Created by JG on 17/11/16.
 */
public class RatingDatastoreMapper {

    public Rating map(com.tripwego.dto.datastore.Rating rating) {
        return new Rating(rating.getRating());
    }
}
