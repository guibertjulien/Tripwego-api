package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.common.mapper.*;
import com.tripwego.api.placeresult.placerating.PlaceRatingRepository;
import com.tripwego.dto.placeresult.PlaceRatingDto;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.appengine.api.datastore.Query.Filter;
import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;
import static com.google.appengine.api.datastore.Query.FilterPredicate;
import static com.tripwego.api.Constants.*;

public class PlaceResultRepository extends AbstractRepository<PlaceResultDto> {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultRepository.class.getName());

    private PlaceResultEntityMapper placeResultEntityMapper = new PlaceResultEntityMapper(new GeoPtEntityMapper());
    private PlaceResultDtoMapper placeResultDtoMapper = new PlaceResultDtoMapper(new PostalAddressDtoMapper(), new CategoryDtoMapper(), new LinkDtoMapper(), new RatingDtoMapper(), new LatLngDtoMapper());
    private PlaceRatingRepository placeRatingRepository = new PlaceRatingRepository();
    private PlaceResultQueries placeResultQueries = new PlaceResultQueries();

    public Entity create(PlaceResultDto placeResult) {
        LOGGER.info("--> create - START");
        Entity entity = null;
        try {
            // check if place already exist
            final Key key = KeyFactory.createKey(KIND_PLACE_RESULT, placeResult.getPlace_id());
            entity = datastore.get(key);
            entity.setProperty(IS_ADMIN_AUTOMATIC, placeResult.isAdminAutomatic());
            // update if exist
            update(entity, placeResult);
        } catch (Exception e) {
            LOGGER.info("-->  exception : " + e.getMessage());
            entity = placeResultEntityMapper.map(placeResult);
            datastore.put(entity);
        }
        LOGGER.info("--> create - END");
        return entity;
    }

    public PlaceResultDto retrieve(String placeKeyString) {
        LOGGER.info("--> retrieve - START : " + placeKeyString);
        PlaceResultDto placeResult = null;
        try {
            final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeKeyString));
            placeResult = placeResultDtoMapper.map(placeResultEntity, "");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("--> retrieve - END");
        return placeResult;
    }

    private void update(Entity entity, PlaceResultDto placeResult) {
        LOGGER.info("--> update - START");
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
        LOGGER.info("--> update - END");
    }

    public void incrementCounterAndInitializeRating(final Trip tripUpdated) {
        try {
            final List<Entity> entitiesToUpdate = new ArrayList();
            final Entity tripEntity = datastore.get(KeyFactory.stringToKey(tripUpdated.getId()));
            final Query stepQuery = new Query(KIND_STEP).setAncestor(tripEntity.getKey());
            stepQuery.addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class));
            final List<Entity> entitiesProjection = datastore.prepare(stepQuery).asList(FetchOptions.Builder.withDefaults());
            final Function<Entity, String> groupById = p -> (String) p.getProperty(PLACE_RESULT_ID);
            final Map<String, Long> entitiesWithCount = entitiesProjection.stream().collect(Collectors.groupingBy(groupById, Collectors.counting()));
            for (Map.Entry<String, Long> entry : entitiesWithCount.entrySet()) {
                final String placeResultId = entry.getKey();
                final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeResultId));
                final MyUser user = tripUpdated.getUser();
                final String userId = user.getUserId();
                final Filter byUser = new FilterPredicate(USER_ID, EQUAL, userId);
                final Query placeRatingQuery = new Query(KIND_PLACE_RATING).setKeysOnly().setAncestor(placeResultEntity.getKey()).setFilter(byUser);
                final List<Entity> placeRatingEntities = datastore.prepare(placeRatingQuery).asList(FetchOptions.Builder.withDefaults());
                if (placeRatingEntities.isEmpty()) {
                    entitiesToUpdate.add(placeRatingRepository.entityToCreate(placeResultEntity, new PlaceRatingDto(user)));
                }
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

    public void updateRating(String placeKeyString, String updaterId, int newRating) {
        final Key placeKey = KeyFactory.stringToKey(placeKeyString);
        try {
            final List<Entity> entitiesToUpdate = new ArrayList<>();
            final Entity placeResultEntity = datastore.get(placeKey);
            //
            final Query query = new Query(KIND_PLACE_RATING).setAncestor(placeKey);
            final List<Entity> placeRatingEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
            final Date dateUpdated = new Date();
            long ratingSum = newRating;
            long evaluatedSum = 1;
            for (Entity placeRatingEntity : placeRatingEntities) {
                final String userId = (String) placeRatingEntity.getProperty(USER_ID);
                final Rating rating = (Rating) placeRatingEntity.getProperty(RATING);
                final boolean isEvaluated = (boolean) placeRatingEntity.getProperty(IS_EVALUATED);
                if (updaterId.equals(userId)) {
                    // update PLACE_RATING
                    placeRatingEntity.setProperty(RATING, new Rating(newRating));
                    placeRatingEntity.setProperty(IS_EVALUATED, true);
                    placeRatingEntity.setProperty(UPDATED_AT, dateUpdated);
                    entitiesToUpdate.add(placeRatingEntity);
                } else if (isEvaluated) {
                    ratingSum += rating.getRating();
                    evaluatedSum++;
                }
            }
            final int average = (int) (ratingSum / evaluatedSum);
            // update PLACE_RESULT
            placeResultEntity.setProperty(RATING, new Rating(average));
            placeResultEntity.setProperty(IS_EVALUATED, true);
            placeResultEntity.setProperty(UPDATED_AT, dateUpdated);
            entitiesToUpdate.add(placeResultEntity);
            datastore.put(entitiesToUpdate);
        } catch (EntityNotFoundException e) {
        }
    }

    public void delete() {
        final List<Entity> placesToDelete = placeResultQueries.findPlaceEntitiesUnused();
        if (!placesToDelete.isEmpty()) {
            deletePlaceEntities(placesToDelete);
        }
    }

    private void deletePlaceEntities(List<Entity> placesToDelete) {
        final List<Key> keysToKill = new ArrayList<>();
        keysToKill.addAll(extractKeys(placesToDelete));
        for (Entity entity : placesToDelete) {
            deleteChild(entity);
        }
        if (!keysToKill.isEmpty()) {
            datastore.delete(keysToKill);
        }
    }

    private void deleteChild(Entity parent) {
        placeRatingRepository.deleteCollection(KIND_PLACE_RATING, parent);
    }

    public void decrementCounter(List<Entity> parentEntities, boolean putEntity) {
        for (Entity entity : parentEntities) {
            final String placeId = String.valueOf(entity.getProperty(PLACE_RESULT_ID));
            final List<Entity> entitiesToUpdate = new ArrayList<>();
            try {
                final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeId));
                long counter = (long) placeResultEntity.getProperty(COUNTER);
                placeResultEntity.setProperty(COUNTER, counter - 1);
                entitiesToUpdate.add(placeResultEntity);
            } catch (EntityNotFoundException e) {
            }
            if (putEntity && !entitiesToUpdate.isEmpty()) {
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
}