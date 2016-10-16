package com.example.tripwegoapi.entity;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.Trip;
import com.example.tripwegoapi.query.TripQueries;
import com.google.appengine.api.datastore.*;

import java.util.*;
import java.util.logging.Logger;

import static com.example.tripwegoapi.Constants.KIND_REGION_COUNTER;

/**
 * Created by JG on 05/06/16.
 */
public class RegionCounterRepository {

    private static final Logger _logger = Logger.getLogger(RegionCounterRepository.class.getName());

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private TripQueries tripQueries = new TripQueries();

    public void refresh() {
        purge();
        init();
    }

    private void purge() {
        _logger.info("--> RegionCounterRepository.purge - START");
        final List<Key> keysToKill = new ArrayList<>();
        final Query query = new Query(KIND_REGION_COUNTER);
        List<Entity> entities = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            keysToKill.add(entity.getKey());
        }
        datastore.delete(keysToKill);
        _logger.info("--> RegionCounterRepository.purge - END");
    }

    private void init() {
        _logger.info("--> RegionCounterRepository.init - START");
        final List<Entity> result = new ArrayList<>();
        final List<String> codes = new ArrayList<>();
        final List<Trip> allTrips = tripQueries.findAllTrips();
        for (Trip trip : allTrips) {
            codes.add(trip.getCountryCode());
        }
        // count duplicated items
        final Map<String, Integer> map = new HashMap<>();
        for (String temp : codes) {
            Integer count = map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }
        // sorted map
        final Map<String, Integer> treeMap = new TreeMap<>(map);
        // loop a Map
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            final Entity entity = new Entity(Constants.KIND_REGION_COUNTER);
            entity.setProperty("code", entry.getKey());
            entity.setProperty("counter", entry.getValue());
            result.add(entity);
        }
        datastore.put(result);
        _logger.info("--> RegionCounterRepository.init - END");
    }
}

