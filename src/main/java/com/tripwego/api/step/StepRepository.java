package com.tripwego.api.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.dto.step.Step;

import java.util.ArrayList;
import java.util.List;

import static com.tripwego.api.Constants.KIND_STEP;
import static com.tripwego.api.Constants.PLACE_RESULT_ID;

public class StepRepository extends AbstractRepository<Step> {

    private StepEntityMapper stepEntityMapper = new StepEntityMapperFactory().create();
    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();

    @Override
    public void createCollection(Entity parent, List<Step> steps) {
        final List<Entity> entities = new ArrayList<>();
        for (Step step : steps) {
            entities.add(entityToCreate(parent, step));
        }
        datastore.put(entities);
    }

    @Override
    public Entity entityToCreate(Entity parent, Step step) {
        final Entity entity = stepEntityMapper.map(step, parent);
        final Entity placeResultEntity = placeResultRepository.create(step.getPlaceResultDto());
        entity.setProperty(PLACE_RESULT_ID, KeyFactory.keyToString(placeResultEntity.getKey()));
        return entity;
    }

    public void deleteCollection(final Entity parent) {
        final List<Key> keysToKill = new ArrayList<>();
        final Query query = new Query(KIND_STEP).setAncestor(parent.getKey());
        final List<Entity> entitiesWithJustKey = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        keysToKill.addAll(extractKeys(entitiesWithJustKey));
        datastore.delete(keysToKill);
    }
}
