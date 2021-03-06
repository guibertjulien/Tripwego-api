package com.tripwego.api.region;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.dto.region.RegionCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JG on 05/06/16.
 */
public class RegionCounterQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private RegionCounterMapper regionCounterMapper = new RegionCounterMapper();

    public List<RegionCounter> findAll() {
        final List<RegionCounter> result = new ArrayList<>();
        final Query query = new Query(Constants.KIND_REGION_COUNTER);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final RegionCounter regionCounter = regionCounterMapper.map(entity);
            result.add(regionCounter);
        }
        return result;
    }
}
