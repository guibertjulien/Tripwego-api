package com.tripwego.api.request.mapper.step;

import com.tripwego.api.request.mapper.datastore.GeoPtDatastoreMapper;

/**
 * Created by JG on 17/11/16.
 */
public class StepEntityMapperFactory {

    public StepEntityMapper create() {
        return new StepEntityMapper(new GeoPtDatastoreMapper());
    }
}
