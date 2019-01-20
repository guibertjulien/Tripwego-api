package com.tripwego.api.tripitem.repository;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.step.StepRepository;
import com.tripwego.dto.tripitem.Rental;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.tripwego.api.Constants.KIND_RENTAL;

/**
 * Created by JG on 12/11/16.
 */
public class RentalRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Rental> rentals, Entity parent) {
        for (Rental accommodation : rentals) {
            final Entity entity = new Entity(KIND_RENTAL, parent.getKey());
            datastore.put(entity);
            stepRepository.createCollection(entity, accommodation.getSteps());
        }
    }

    public void updateAll(List<Rental> rentals, Entity parent) {
        removeAll(parent);
        createAll(rentals, parent);
    }

    public void removeAll(Entity parent) {
        final Query query = new Query(KIND_RENTAL).setAncestor(parent.getKey());
        final List<Entity> entitiesWithJustKey = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        final Collection<Key> keysToKill = entitiesWithJustKey.stream().map(entity -> entity.getKey()).collect(Collectors.toList());
        for (Entity entity : entitiesWithJustKey) {
            stepRepository.deleteCollection(entity);
        }
        datastore.delete(keysToKill);
    }
}
