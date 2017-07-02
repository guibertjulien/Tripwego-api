package com.tripwego.api.step;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.dto.step.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.KIND_STEP;
import static com.tripwego.api.Constants.PLACE_RESULT_ID_FOR_STEP;

public class StepRepository extends AbstractRepository<Step> {

    private static final Logger LOGGER = Logger.getLogger(StepRepository.class.getName());

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
        entity.setProperty(PLACE_RESULT_ID_FOR_STEP, KeyFactory.keyToString(placeResultEntity.getKey()));
        return entity;
    }

    public void deleteCollection(final Entity parent) {
        final List<Key> keysToKill = new ArrayList<>();
        final Query query = new Query(KIND_STEP).setAncestor(parent.getKey());
        final List<Entity> stepEntitiesToDelete = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        keysToKill.addAll(extractKeys(stepEntitiesToDelete));
        datastore.delete(keysToKill);
        // after step deletion
        placeResultRepository.deletePlaceAssociated(stepEntitiesToDelete, KIND_STEP, PLACE_RESULT_ID_FOR_STEP);
    }
}
