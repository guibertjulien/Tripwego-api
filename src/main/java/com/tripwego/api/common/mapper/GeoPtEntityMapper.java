package com.tripwego.api.common.mapper;

import com.google.appengine.api.datastore.GeoPt;
import com.tripwego.dto.common.LatLngDto;

/**
 * Created by JG on 17/11/16.
 */
public class GeoPtEntityMapper {

    public GeoPt map(LatLngDto latLngDto) {
        return new GeoPt(latLngDto.getLatitude().floatValue(), latLngDto.getLongitude().floatValue());
    }
}
