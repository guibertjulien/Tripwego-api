package com.tripwego.api.entity;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;
import com.tripwego.api.Constants;
import com.tripwego.dto.tripitem.Accommodation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JG on 12/11/16.
 */
public class AccommodationRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Accommodation> accommodations, Entity parent) {
        final List<Entity> entities = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            final Entity entity = new Entity(Constants.KIND_ACCOMMODATION, parent.getKey());
            stepRepository.createAll(accommodation.getSteps(), parent);
            entities.add(entity);
        }
        datastore.put(entities);
    }

    public void updateAll(List<Accommodation> accommodations, Entity parent) {
        removeAll(parent);
        createAll(accommodations, parent);
    }

    private void removeAll(Entity parent) {
        final Query query = new Query(Constants.KIND_ACCOMMODATION).setAncestor(parent.getKey());
        final List<Entity> entitiesWithJustKey = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        final ImmutableCollection<Key> keysToKill = FluentIterable.from(entitiesWithJustKey).toMap(new Function<Entity, Key>() {
            @Override
            public Key apply(Entity entity) {
                return entity.getKey();
            }
        }).values();
        datastore.delete(keysToKill);
    }
}
