package com.tripwego.api.tripitem.repository;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;
import com.tripwego.api.step.StepRepository;
import com.tripwego.dto.tripitem.Activity;

import java.util.List;

import static com.tripwego.api.Constants.KIND_ACTIVITY;

/**
 * Created by JG on 12/11/16.
 */
public class ActivityRepository {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final StepRepository stepRepository = new StepRepository();

    public void createAll(List<Activity> activities, Entity parent) {
        for (Activity activity : activities) {
            final Entity entity = new Entity(KIND_ACTIVITY, parent.getKey());
            datastore.put(entity);
            stepRepository.createCollection(entity, activity.getSteps());
        }
    }

    public void updateAll(List<Activity> activities, Entity parent) {
        deleteAll(parent);
        createAll(activities, parent);
    }

    public void deleteAll(Entity parent) {
        final Query query = new Query(KIND_ACTIVITY).setAncestor(parent.getKey());
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