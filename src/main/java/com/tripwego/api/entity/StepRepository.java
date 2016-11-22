package com.tripwego.api.entity;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;
import com.tripwego.api.Constants;
import com.tripwego.api.request.mapper.step.StepEntityMapper;
import com.tripwego.api.request.mapper.step.StepEntityMapperFactory;
import com.tripwego.domain.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JG on 05/06/16.
 */
public class StepRepository {

    private StepEntityMapper stepEntityMapper = new StepEntityMapperFactory().create();

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public void createAll(List<Step> steps, Entity parent) {
        final List<Entity> entities = new ArrayList<>();
        long indexOnRoadMap = 0;
        for (Step step : steps) {
            final Entity entity = stepEntityMapper.map(step, parent);
            entity.setProperty(Constants.INDEX_ON_ROAD_MAP, indexOnRoadMap);
            entities.add(entity);
            indexOnRoadMap++;
        }
        datastore.put(entities);
    }

    public void updateAll(List<Step> steps, Entity parent) {
        removeAll(parent);
        createAll(steps, parent);
    }

    private void removeAll(Entity parent) {
        final Query query = new Query(Constants.KIND_STEP).setAncestor(parent.getKey());
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
