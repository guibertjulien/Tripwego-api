package com.tripwego.api.request.mapper.trip;

import com.tripwego.api.request.mapper.datastore.CategoryDatastoreMapper;
import com.tripwego.api.request.mapper.datastore.LinkDatastoreMapper;
import com.tripwego.api.request.mapper.datastore.RatingDatastoreMapper;

/**
 * Created by JG on 17/11/16.
 */
public class TripEntityMapperFactory {

    public TripEntityMapper create() {
        return new TripEntityMapper(new CategoryDatastoreMapper(), new RatingDatastoreMapper(), new LinkDatastoreMapper());
    }
}
