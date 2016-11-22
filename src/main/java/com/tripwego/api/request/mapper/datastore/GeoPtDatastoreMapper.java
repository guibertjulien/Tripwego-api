package com.tripwego.api.request.mapper.datastore;

import com.google.appengine.api.datastore.GeoPt;

/**
 * Created by JG on 17/11/16.
 */
public class GeoPtDatastoreMapper {

    public GeoPt map(com.tripwego.domain.datastore.GeoPt geoPt) {
        return new GeoPt(geoPt.getLatitude().floatValue(), geoPt.getLongitude().floatValue());
    }
}
