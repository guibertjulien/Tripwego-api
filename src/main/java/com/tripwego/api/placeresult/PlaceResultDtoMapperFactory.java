package com.tripwego.api.placeresult;

import com.tripwego.api.common.mapper.*;

/**
 * Created by JG on 17/11/16.
 */
public class PlaceResultDtoMapperFactory {

    public PlaceResultDtoMapper create() {
        return new PlaceResultDtoMapper(new PostalAddressDtoMapper(),
                new CategoryDtoMapper(),
                new LinkDtoMapper(),
                new RatingDtoMapper(),
                new LatLngDtoMapper());
    }
}
