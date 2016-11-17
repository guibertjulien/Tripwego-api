package com.tripwego.api.response.mapper.step;

import com.tripwego.api.response.mapper.datastore.*;

/**
 * Created by JG on 17/11/16.
 */
public class StepMapperFactory {

    public StepMapper create() {
        return new StepMapper(new PostalAddressMapper(), new CategoryMapper(), new LinkMapper(), new RatingMapper(), new GeoPtMapper());
    }
}
