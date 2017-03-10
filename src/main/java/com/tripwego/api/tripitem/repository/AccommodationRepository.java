package com.tripwego.api.tripitem.repository;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;
import com.tripwego.api.step.StepRepository;
import com.tripwego.dto.tripitem.Accommodation;

import java.util.List;

import static com.tripwego.api.Constants.KIND_ACCOMMODATION;

/**
 * Created by JG on 12/11/16.
 */
public class AccommodationRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Accommodation> accommodations, Entity parent) {
        for (Accommodation accommodation : accommodations) {
            final Entity entity = new Entity(KIND_ACCOMMODATION, parent.getKey());
            datastore.put(entity);
            stepRepository.createCollection(entity, accommodation.getSteps());
        }
    }

    public void updateAll(List<Accommodation> accommodations, Entity parent) {
        removeAll(parent);
        createAll(accommodations, parent);
    }

    public void removeAll(Entity parent) {
        final Query query = new Query(KIND_ACCOMMODATION).setAncestor(parent.getKey());
        final List<Entity> entitiesWithJustKey = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        final ImmutableCollection<Key> keysToKill = FluentIterable.from(entitiesWithJustKey).toMap(new Function<Entity, Key>() {
            @Override
            public Key apply(Entity entity) {
                return entity.getKey();
            }
        }).values();
        for (Entity entity : entitiesWithJustKey) {
            stepRepository.deleteCollection(entity);
        }
        datastore.delete(keysToKill);
    }


}
