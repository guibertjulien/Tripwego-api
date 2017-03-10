package com.tripwego.api.common.mapper;

import com.google.appengine.api.datastore.GeoPt;
import com.tripwego.dto.common.LatLngDto;

/**
 * Created by JG on 17/11/16.
 */
public class LatLngDtoMapper {

    public LatLngDto map(GeoPt geoPt) {
        return new LatLngDto(Double.valueOf(geoPt.getLatitude()),
                Double.valueOf(geoPt.getLongitude()));
    }
}
