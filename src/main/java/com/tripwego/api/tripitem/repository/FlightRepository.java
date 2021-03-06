package com.tripwego.api.tripitem.repository;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.step.StepRepository;
import com.tripwego.dto.tripitem.Flight;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.tripwego.api.Constants.KIND_FLIGHT;

/**
 * Created by JG on 12/11/16.
 */
public class FlightRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Flight> flights, Entity parent) {
        for (Flight flight : flights) {
            final Entity entity = new Entity(KIND_FLIGHT, parent.getKey());
            datastore.put(entity);
            stepRepository.createCollection(entity, flight.getSteps());
        }
    }

    public void updateAll(List<Flight> flights, Entity parent) {
        removeAll(parent);
        createAll(flights, parent);
    }

    public void removeAll(Entity parent) {
        final Query query = new Query(KIND_FLIGHT).setAncestor(parent.getKey());
        final List<Entity> entitiesWithJustKey = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        final Collection<Key> keysToKill = entitiesWithJustKey.stream().map(new java.util.function.Function<Entity, Key>() {
            @Override
            public Key apply(Entity entity) {
                return entity.getKey();
            }
        }).collect(Collectors.toList());
        for (Entity entity : entitiesWithJustKey) {
            stepRepository.deleteCollection(entity);
        }
        datastore.delete(keysToKill);
    }


}
