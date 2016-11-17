package com.tripwego.api.response.mapper.trip;

import com.tripwego.api.response.mapper.datastore.CategoryMapper;
import com.tripwego.api.response.mapper.datastore.LinkMapper;
import com.tripwego.api.response.mapper.datastore.RatingMapper;

/**
 * Created by JG on 17/11/16.
 */
public class TripMapperFactory {

    public TripMapper create() {
        return new TripMapper(new CategoryMapper(), new LinkMapper(), new RatingMapper());
    }
}
