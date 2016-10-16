package com.example.tripwegoapi.query;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.RegionCounter;
import com.example.tripwegoapi.response.mapper.RegionCounterMapper;
import com.google.appengine.api.datastore.*;

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
