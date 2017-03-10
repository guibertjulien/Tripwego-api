package com.tripwego.api.tripitem.repository;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;
import com.tripwego.api.step.StepRepository;
import com.tripwego.dto.tripitem.Rail;

import java.util.List;

import static com.tripwego.api.Constants.KIND_RAIL;

/**
 * Created by JG on 12/11/16.
 */
public class RailRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Rail> rails, Entity parent) {
        for (Rail flight : rails) {
            final Entity entity = new Entity(KIND_RAIL, parent.getKey());
            datastore.put(entity);
            stepRepository.createCollection(entity, flight.getSteps());
        }
    }

    public void updateAll(List<Rail> rails, Entity parent) {
        removeAll(parent);
        createAll(rails, parent);
    }

    public void removeAll(Entity parent) {
        final Query query = new Query(KIND_RAIL).setAncestor(parent.getKey());
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
