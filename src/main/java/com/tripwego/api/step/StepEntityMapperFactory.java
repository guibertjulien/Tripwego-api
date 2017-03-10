package com.tripwego.api.step;

import com.tripwego.api.common.mapper.GeoPtEntityMapper;

/**
 * Created by JG on 17/11/16.
 */
public class StepEntityMapperFactory {

    public StepEntityMapper create() {
        return new StepEntityMapper(new GeoPtEntityMapper());
    }
}
