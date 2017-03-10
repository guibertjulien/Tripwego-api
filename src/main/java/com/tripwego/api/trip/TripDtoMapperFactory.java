package com.tripwego.api.trip;

import com.tripwego.api.common.mapper.CategoryDtoMapper;
import com.tripwego.api.common.mapper.LinkDtoMapper;
import com.tripwego.api.common.mapper.RatingDtoMapper;

/**
 * Created by JG on 17/11/16.
 */
public class TripDtoMapperFactory {

    public TripDtoMapper create() {
        return new TripDtoMapper(new CategoryDtoMapper(),
                new LinkDtoMapper(),
                new RatingDtoMapper());
    }
}
