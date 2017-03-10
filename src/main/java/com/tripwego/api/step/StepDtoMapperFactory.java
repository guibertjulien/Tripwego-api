package com.tripwego.api.step;

import com.tripwego.api.common.mapper.*;

/**
 * Created by JG on 17/11/16.
 */
public class StepDtoMapperFactory {

    public StepDtoMapper create() {
        return new StepDtoMapper(new PostalAddressDtoMapper(),
                new CategoryDtoMapper(),
                new LinkDtoMapper(),
                new RatingDtoMapper(),
                new LatLngDtoMapper());
    }
}
