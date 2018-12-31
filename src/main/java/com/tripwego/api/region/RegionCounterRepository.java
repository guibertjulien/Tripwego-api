package com.tripwego.api.region;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.trip.TripQueries;
import com.tripwego.dto.trip.Trip;

import java.util.*;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 05/06/16.
 */
public class RegionCounterRepository {

    private static final Logger LOGGER = Logger.getLogger(RegionCounterRepository.class.getName());

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private TripQueries tripQueries = new TripQueries();

    public void refresh() {
        purge();
        init();
    }

    private void purge() {
        LOGGER.info("--> RegionCounterRepository.purge - START");
        final List<Key> keysToKill = new ArrayList<>();
        final Query query = new Query(KIND_REGION_COUNTER);
        List<Entity> entities = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            keysToKill.add(entity.getKey());
        }
        datastore.delete(keysToKill);
        LOGGER.info("--> RegionCounterRepository.purge - END");
    }

    private void init() {
        LOGGER.info("--> RegionCounterRepository.init - START");
        final List<Entity> result = new ArrayList<>();
        final List<String> codes = new ArrayList<>();
        final List<Trip> allTrips = tripQueries.findAllTripsAlive(0, 0);
        for (Trip trip : allTrips) {
            codes.add(trip.getCountryCode());
        }
        // updateCounter duplicated items
        final Map<String, Integer> map = new HashMap<>();
        for (String temp : codes) {
            Integer count = map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }
        // sorted map
        final Map<String, Integer> treeMap = new TreeMap<>(map);
        // loop a Map
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            final Entity entity = new Entity(KIND_REGION_COUNTER);
            entity.setProperty(CODE, entry.getKey());
            entity.setProperty(COUNTER, entry.getValue());
            result.add(entity);
        }
        datastore.put(result);
        LOGGER.info("--> RegionCounterRepository.init - END");
    }
}

