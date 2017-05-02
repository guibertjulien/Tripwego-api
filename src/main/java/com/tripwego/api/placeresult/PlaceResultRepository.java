package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.common.mapper.*;
import com.tripwego.api.placeresult.addresscomponent.AddressComponentDtoMapper;
import com.tripwego.api.placeresult.addresscomponent.AddressComponentQueries;
import com.tripwego.api.placeresult.addresscomponent.AddressComponentRepository;
import com.tripwego.api.placeresult.placerating.PlaceRatingRepository;
import com.tripwego.dto.placeresult.AddressComponentDto;
import com.tripwego.dto.placeresult.PlaceRatingDto;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;

import java.util.*;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.Query.*;
import static com.tripwego.api.Constants.*;

public class PlaceResultRepository extends AbstractRepository<PlaceResultDto> {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultRepository.class.getName());

    protected final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private PlaceResultEntityMapper placeResultEntityMapper = new PlaceResultEntityMapper(new GeoPtEntityMapper());
    private PlaceResultDtoMapper placeResultDtoMapper = new PlaceResultDtoMapper(new PostalAddressDtoMapper(), new CategoryDtoMapper(), new LinkDtoMapper(), new RatingDtoMapper(), new LatLngDtoMapper());

    private AddressComponentRepository addressComponentRepository = new AddressComponentRepository();
    private AddressComponentQueries addressComponentQueries = new AddressComponentQueries();
    private AddressComponentDtoMapper addressComponentDtoMapper = new AddressComponentDtoMapper();

    private PlaceRatingRepository placeRatingRepository = new PlaceRatingRepository();

    public Entity create(PlaceResultDto placeResult) {
        LOGGER.info("--> create - START");
        Entity entity = null;
        try {
            // check if place already exist
            final Key key = KeyFactory.createKey(KIND_PLACE_RESULT, placeResult.getPlace_id());
            entity = datastore.get(key);
            // update if exist
            update(entity, placeResult);
        } catch (Exception e) {
            LOGGER.info("-->  exception : " + e.getMessage());
            entity = placeResultEntityMapper.map(placeResult);
            datastore.put(entity);
            addressComponentRepository.updateCollection(KIND_ADDRESS_COMPONENT, entity, placeResult.getAddress_components());
        }
        LOGGER.info("--> create - END");
        return entity;
    }

    public PlaceResultDto retrieveEager(String placeKeyString) {
        LOGGER.info("--> retrieveEager - START : " + placeKeyString);
        PlaceResultDto placeResult = null;
        try {
            final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeKeyString));
            placeResult = placeResultDtoMapper.map(placeResultEntity);
            for (Entity entity : addressComponentQueries.find(placeResultEntity, KIND_ADDRESS_COMPONENT)) {
                final AddressComponentDto addressComponent = addressComponentDtoMapper.map(entity);
                placeResult.getAddress_components().add(addressComponent);
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("--> retrieveEager - END");
        return placeResult;
    }

    private void update(Entity entity, PlaceResultDto placeResult) {
        LOGGER.info("--> update - START");
        final Set<String> categories = new HashSet<>();
        final Set<String> types = new HashSet<>();
        if (entity.getProperty(STEP_CATEGORIES) != null) {
            Object property = entity.getProperty(STEP_CATEGORIES);
            categories.addAll((ArrayList<String>) property);
        }
        if (entity.getProperty(STEP_TYPES) != null) {
            types.addAll((ArrayList<String>) entity.getProperty(STEP_TYPES));
        }
        categories.addAll(placeResult.getStepCategories());
        entity.setProperty(STEP_CATEGORIES, categories);
        types.addAll(placeResult.getStepTypes());
        entity.setProperty(STEP_TYPES, types);
        datastore.put(entity);
        LOGGER.info("--> update - END");
    }

    public void updateCounterAndInitRating(final Trip tripUpdated) {
        LOGGER.info("--> updateCounterAndInitRating - START");
        try {
            final List<Entity> entitiesToUpdate = new ArrayList();
            final Entity tripEntity = datastore.get(KeyFactory.stringToKey(tripUpdated.getId()));
            final Query stepQuery = new Query(KIND_STEP).setAncestor(tripEntity.getKey());
            stepQuery.addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class));
            stepQuery.setDistinct(true);
            final List<Entity> stepsEntitiesProjection = datastore.prepare(stepQuery).asList(FetchOptions.Builder.withDefaults());
            for (Entity stepEntityProjection : stepsEntitiesProjection) {
                final String placeResultId = (String) stepEntityProjection.getProperty(PLACE_RESULT_ID);
                final Entity placeResultEntity = datastore.get(KeyFactory.stringToKey(placeResultId));
                final MyUser user = tripUpdated.getUser();
                final String userId = user.getUserId();
                final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
                final Query placeRatingQuery = new Query(KIND_PLACE_RATING).setKeysOnly().setAncestor(placeResultEntity.getKey()).setFilter(byUser);
                final List<Entity> placeRatingEntities = datastore.prepare(placeRatingQuery).asList(FetchOptions.Builder.withDefaults());
                if (placeRatingEntities.isEmpty()) {
                    entitiesToUpdate.add(placeRatingRepository.entityToCreate(placeResultEntity, new PlaceRatingDto(user)));
                    long counter = (long) placeResultEntity.getProperty(COUNTER);
                    placeResultEntity.setProperty(COUNTER, counter + 1);
                    entitiesToUpdate.add(placeResultEntity);
                    LOGGER.info("--> place (" + placeResultId + ") not exist for user : counter property UPDATED and PLACE_RATING kind created");
                } else {
                    LOGGER.info("--> place (" + placeResultId + ") already exist for user : NO UPDATE");
                }
            }
            datastore.put(entitiesToUpdate);
        } catch (EntityNotFoundException e) {
            LOGGER.info("--> error : " + e.getMessage());
        }
        LOGGER.info("--> updateCounterAndInitRating - END");
    }

    @Override
    protected void createCollection(Entity parent, List<PlaceResultDto> collection) {

    }

    @Override
    public Entity entityToCreate(Entity parent, PlaceResultDto placeResultDto) {
        return null;
    }

    public void updateRating(String placeKeyString, String updaterId, int newRating) {
        LOGGER.info("--> updateRating - START");
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
        LOGGER.info("--> updateRating - END");
    }
}