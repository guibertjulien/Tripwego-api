package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.Rating;

/**
 * Created by JG on 17/11/16.
 */
public class RatingMapper {

    public int map(Object property) {
        int result = 0;
        if (property != null) {
            final Rating rating = (Rating) property;
            result = rating.getRating();
        }
        return result;
    }
}
