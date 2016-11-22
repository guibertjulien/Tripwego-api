package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.GeoPt;

/**
 * Created by JG on 17/11/16.
 */
public class GeoPtMapper {

    public com.tripwego.domain.datastore.GeoPt map(GeoPt geoPt) {
        final com.tripwego.domain.datastore.GeoPt result = new com.tripwego.domain.datastore.GeoPt();
        result.setLatitude(Double.valueOf(geoPt.getLatitude()));
        result.setLongitude(Double.valueOf(geoPt.getLongitude()));
        return result;
    }
}
