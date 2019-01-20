package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.common.mapper.*;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.trip.Trip;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.tripwego.api.Constants.*;

public class PlaceResultRepository extends AbstractRepository<PlaceResultDto> {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultRepository.class.getName());

    private PlaceResultEntityMapper placeResultEntityMapper = new PlaceResultEntityMapper(new GeoPtEntityMapper());
    private PlaceResultDtoMapper placeResultDtoMapper = new PlaceResultDtoMapper(new PostalAddressDtoMapper(), new CategoryDtoMapper(), new LinkDtoMapper(), new RatingDtoMapper(), new LatLngDtoMapper());
    private PlaceResultQueries placeResultQueries = new PlaceResultQueries();

    public Entity create(PlaceResultDto placeResult) {
        Entity entity = null;
        try {
            // check if place already exist
            final Key key = KeyFactory.createKey(KIND_PLACE, placeResult.getPlace_id());
            entity = datastore.get(key);
            // update if exist
            update(entity, placeResult);
        } catch (Exception e) {
            // creation
            entity = placeResultEntityMapper.map(placeResult);
            // IS_ADMIN_AUTOMATIC just for creation
            entity.setProperty(IS_ADMIN_AUTOMATIC, placeResult.isAdminAutomatic());
            datastore.put(entity);
        }
        return entity;
    }

    public PlaceResultDto retrieve(String placeKeyString) {
        PlaceResultDto placeResult = null;
        try {
            final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeKeyString));
            placeResult = placeResultDtoMapper.map(placeResultEntity, "");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return placeResult;
    }

    private void update(Entity entity, PlaceResultDto placeResult) {
        final Set<String> stepCategories = new HashSet<>();
        final Set<String> stepTypes = new HashSet<>();
        final Set<String> suggestionTypes = new HashSet<>();
        final Set<String> types = new HashSet<>();
        if (entity.getProperty(STEP_CATEGORIES) != null) {
            Object property = entity.getProperty(STEP_CATEGORIES);
            stepCategories.addAll((ArrayList<String>) property);
        }
        if (entity.getProperty(STEP_TYPES) != null) {
            stepTypes.addAll((ArrayList<String>) entity.getProperty(STEP_TYPES));
        }
        if (entity.getProperty(SUGGESTION_TYPES) != null) {
            suggestionTypes.addAll((ArrayList<String>) entity.getProperty(SUGGESTION_TYPES));
        }
        if (entity.getProperty(TYPES) != null) {
            types.addAll((ArrayList<String>) entity.getProperty(TYPES));
        }
        stepCategories.addAll(placeResult.getStepCategories());
        entity.setProperty(STEP_CATEGORIES, stepCategories);
        stepTypes.addAll(placeResult.getStepTypes());
        entity.setProperty(STEP_TYPES, stepTypes);
        suggestionTypes.addAll(placeResult.getSuggestionTypes());
        entity.setProperty(SUGGESTION_TYPES, suggestionTypes);
        types.addAll(placeResult.getTypes());
        entity.setProperty(TYPES, types);
        if (placeResult.getPopulation() > 0) {
            entity.setProperty(POPULATION, placeResult.getPopulation());
        }
        placeResultEntityMapper.updateTranslation(entity, placeResult);
        datastore.put(entity);
    }

    public void incrementCounter(final Trip trip) {
        try {
            final List<Entity> entitiesToUpdate = new ArrayList();
            final Entity tripEntity = datastore.get(KeyFactory.stringToKey(trip.getId()));
            final Query stepQuery = new Query(KIND_STEP).setAncestor(tripEntity.getKey());
            stepQuery.addProjection(new PropertyProjection(PLACE_KEY, String.class));
            final List<Entity> entitiesProjection = datastore.prepare(stepQuery).asList(FetchOptions.Builder.withDefaults());
            final Function<Entity, String> groupById = p -> (String) p.getProperty(PLACE_KEY);
            final Map<String, Long> entitiesWithCount = entitiesProjection.stream().collect(Collectors.groupingBy(groupById, Collectors.counting()));
            for (Map.Entry<String, Long> entry : entitiesWithCount.entrySet()) {
                final String placeResultId = entry.getKey();
                final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeResultId));
                incrementCounter(placeResultEntity, false, entry.getValue());
                entitiesToUpdate.add(placeResultEntity);
            }
            datastore.put(entitiesToUpdate);
        } catch (EntityNotFoundException e) {
        }
    }

    @Override
    protected void createCollection(Entity parent, List<PlaceResultDto> collection) {

    }

    @Override
    public Entity entityToCreate(Entity parent, PlaceResultDto placeResultDto) {
        return null;
    }

    public void delete() {
        final List<Key> keysToKill = placeResultQueries.findPlaceEntitiesUnused();
        if (!keysToKill.isEmpty()) {
            datastore.delete(keysToKill);
        }
    }

    public void decrementCounter(List<Entity> parentEntities) {
        for (Entity entity : parentEntities) {
            final String placeId = String.valueOf(entity.getProperty(PLACE_KEY));
            final List<Entity> entitiesToUpdate = new ArrayList<>();
            try {
                final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeId));
                long counter = (long) placeResultEntity.getProperty(COUNTER);
                placeResultEntity.setProperty(COUNTER, counter - 1);
                entitiesToUpdate.add(placeResultEntity);
            } catch (EntityNotFoundException e) {
            }
            if (!entitiesToUpdate.isEmpty()) {
                datastore.put(entitiesToUpdate);
            }
        }
    }

    public void incrementCounter(Entity placeResultEntity, boolean putEntity, long increment) {
        final long counter = (long) placeResultEntity.getProperty(COUNTER);
        placeResultEntity.setProperty(COUNTER, counter + increment);
        if (putEntity) {
            datastore.put(placeResultEntity);
        }
    }

    public void updateSuggestionTypes(PlaceResultDto placeResultDto) {
        final Entity entity;
        try {
            entity = datastore.get(KeyFactory.stringToKey(placeResultDto.getPlaceKey()));
            entity.setProperty(SUGGESTION_TYPES, placeResultDto.getSuggestionTypes());
            entity.setProperty(UPDATED_AT, new Date());
            datastore.put(entity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }
}