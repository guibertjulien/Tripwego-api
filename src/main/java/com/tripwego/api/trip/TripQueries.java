package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.api.step.StepDtoMapper;
import com.tripwego.api.step.StepDtoMapperFactory;
import com.tripwego.api.user.UserQueries;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.user.MyUser;
import com.tripwego.dto.user.Traveler;

import java.util.ArrayList;
import java.util.List;

import static com.google.appengine.api.datastore.Query.*;
import static com.tripwego.api.Constants.*;


/**
 * Created by JG on 04/06/16.
 */
public class TripQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private TripDtoMapper tripDtoMapper = new TripDtoMapperFactory().create();
    private StepDtoMapper stepDtoMapper = new StepDtoMapperFactory().create();
    private UserQueries userQueries = new UserQueries();
    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();

    public List<Trip> findTripsByUser(String userId) {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
        //final Query.Filter notCancelled = new Query.FilterPredicate(IS_CANCELLED, Query.FilterOperator.NOT_EQUAL, true);
        final Filter isDefaultVersion = new FilterPredicate(IS_DEFAULT, FilterOperator.EQUAL, true);
        final Filter filters = CompositeFilterOperator.and(byUser, isDefaultVersion);
        // query
        final Query query = new Query(KIND_TRIP).setFilter(filters)
                .addSort(IS_CANCELLED, SortDirection.ASCENDING)
                .addSort(CREATED_AT, SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final Trip trip = tripDtoMapper.map(entity, Optional.<MyUser>absent());
            result.add(trip);
        }
        return result;
    }

    public List<Trip> findAllTrips() {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Filter notCancelled = new FilterPredicate(IS_CANCELLED, FilterOperator.NOT_EQUAL, true);
        final Filter isDefaultVersion = new FilterPredicate(IS_DEFAULT, FilterOperator.EQUAL, true);
        final Filter isPublic = new FilterPredicate(IS_PRIVATE, FilterOperator.EQUAL, false);
        final Filter isPublished = new FilterPredicate(IS_PUBLISHED, FilterOperator.EQUAL, true);
        //final Query.Filter isUserKnown = new Query.FilterPredicate(USER_ID, Query.FilterOperator.NOT_EQUAL, null);
        final Filter filters = CompositeFilterOperator.and(notCancelled, isDefaultVersion, isPublic, isPublished);
        // query
        final Query query = new Query(KIND_TRIP).setFilter(filters)
                .addSort(IS_CANCELLED, SortDirection.ASCENDING)
                .addSort(CREATED_AT, SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        // because "Cannot have inequality filters on multiple properties"
        final ImmutableList<Entity> entitiesFiltered = FluentIterable.from(entities).filter(new Predicate<Entity>() {
            @Override
            public boolean apply(Entity entity) {
                return entity.getProperty(USER_ID) != null;
            }
        }).toList();
        for (Entity entity : entitiesFiltered) {
            Optional<MyUser> user = Optional.absent();
            if (entity.getProperty(USER_ID) != null) {
                final String userId = String.valueOf(entity.getProperty(USER_ID));
                user = userQueries.findByUserId(userId);
            }
            final Trip trip = tripDtoMapper.map(entity, user);
            result.add(trip);
        }
        return result;
    }

    public List<Step> findStepsByTripItem(Entity parent) {
        final List<Step> result = new ArrayList<>();
        final List<Entity> entities = findStepEntitiesKeyByTripItem(parent);
        for (Entity stepEntity : entities) {
            final Step step = stepDtoMapper.map(stepEntity);
            step.setPlaceResultDto(placeResultRepository.retrieveEager(step.getPlaceResultId()));
            result.add(step);
        }
        return result;
    }

    public List<Traveler> findTravelersByTrip(Entity tripEntity) {
        final List<Traveler> result = new ArrayList<>();
        // TODO
        return result;
    }

    public List<Trip> findTripsVersionByTrip(Entity tripEntity) {
        final List<Trip> result = new ArrayList<>();
        // TODO
        return result;
    }

    // TODO sort ?
    public List<Entity> findStepEntitiesKeyByTripItem(Entity parent) {
        final Query query = new Query(KIND_STEP).setAncestor(parent.getKey()).addSort(INDEX_ON_ROAD_MAP);
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesWithUserUnknown() {
        final Filter isUserUnknown = new FilterPredicate(USER_ID, FilterOperator.EQUAL, null);
        final Query query = new Query(KIND_TRIP).setFilter(isUserUnknown);
        return datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelled() {
        final Filter isCancelled = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Query query = new Query(KIND_TRIP).setFilter(isCancelled);
        return datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelledByUser(String userId) {
        final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
        final Filter isCancelled = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Filter filters = CompositeFilterOperator.and(byUser, isCancelled);
        final Query query = new Query(KIND_TRIP).setFilter(filters);
        return datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
    }
}
