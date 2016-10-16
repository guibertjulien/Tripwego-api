package com.example.tripwegoapi.query;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.MyUser;
import com.example.tripwegoapi.dto.Step;
import com.example.tripwegoapi.dto.Traveler;
import com.example.tripwegoapi.dto.Trip;
import com.example.tripwegoapi.response.mapper.StepMapper;
import com.example.tripwegoapi.response.mapper.TripMapper;
import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by JG on 04/06/16.
 */
public class TripQueries {
    private static final Logger _logger = Logger.getLogger(TripQueries.class.getName());
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private TripMapper tripMapper = new TripMapper();
    private StepMapper stepMapper = new StepMapper();
    private UserQueries userQueries = new UserQueries();

    public List<Trip> findTripsByUser(String userId) {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Query.Filter byUser = new Query.FilterPredicate(Constants.USER_ID, Query.FilterOperator.EQUAL, userId);
        //final Query.Filter notCancelled = new Query.FilterPredicate(IS_CANCELLED, Query.FilterOperator.NOT_EQUAL, true);
        final Query.Filter isDefaultVersion = new Query.FilterPredicate(Constants.IS_DEFAULT, Query.FilterOperator.EQUAL, true);
        final Query.Filter filters = Query.CompositeFilterOperator.and(byUser, isDefaultVersion);
        // query
        final Query query = new Query(Constants.KIND_TRIP).setFilter(filters)
                .addSort(Constants.IS_CANCELLED, Query.SortDirection.ASCENDING)
                .addSort(Constants.CREATED_AT, Query.SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final Trip trip = tripMapper.map(entity, Optional.<List<Step>>absent(), Optional.<MyUser>absent());
            result.add(trip);
        }
        return result;
    }

    public List<Trip> findAllTrips() {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Query.Filter notCancelled = new Query.FilterPredicate(Constants.IS_CANCELLED, Query.FilterOperator.NOT_EQUAL, true);
        final Query.Filter isDefaultVersion = new Query.FilterPredicate(Constants.IS_DEFAULT, Query.FilterOperator.EQUAL, true);
        final Query.Filter isPublic = new Query.FilterPredicate(Constants.IS_PRIVATE, Query.FilterOperator.EQUAL, false);
        final Query.Filter isPublished = new Query.FilterPredicate(Constants.IS_PUBLISHED, Query.FilterOperator.EQUAL, true);
        //final Query.Filter isUserKnown = new Query.FilterPredicate(USER_ID, Query.FilterOperator.NOT_EQUAL, null);
        final Query.Filter filters = Query.CompositeFilterOperator.and(notCancelled, isDefaultVersion, isPublic, isPublished);
        // query
        final Query query = new Query(Constants.KIND_TRIP).setFilter(filters)
                .addSort(Constants.IS_CANCELLED, Query.SortDirection.ASCENDING)
                .addSort(Constants.CREATED_AT, Query.SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        // because "Cannot have inequality filters on multiple properties"
        final ImmutableList<Entity> entitiesFiltered = FluentIterable.from(entities).filter(new Predicate<Entity>() {
            @Override
            public boolean apply(Entity entity) {
                return entity.getProperty(Constants.USER_ID) != null;
            }
        }).toList();
        for (Entity entity : entitiesFiltered) {
            Optional<MyUser> user = Optional.absent();
            if (entity.getProperty(Constants.USER_ID) != null) {
                final String userId = String.valueOf(entity.getProperty(Constants.USER_ID));
                user = userQueries.findByUserId(userId);
            }
            final Trip trip = tripMapper.map(entity, Optional.<List<Step>>absent(), user);
            result.add(trip);
        }
        return result;
    }

    public List<Step> findStepsByTrip(Entity parent) {
        final List<Step> result = new ArrayList<>();
        final List<Entity> entities = findStepEntitiesKeyByTrip(parent);
        for (Entity entity : entities) {
            final Step step = stepMapper.map(entity);
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

    public List<Entity> findStepEntitiesKeyByTrip(Entity parent) {
        final Query query = new Query(Constants.KIND_STEP).setAncestor(parent.getKey()).addSort(Constants.INDEX_ON_ROAD_MAP);
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesWithUserUnknown() {
        final Query.Filter isUserUnknown = new Query.FilterPredicate(Constants.USER_ID, Query.FilterOperator.EQUAL, null);
        final Query query = new Query(Constants.KIND_TRIP).setFilter(isUserUnknown);
        return datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelled() {
        final Query.Filter isCancelled = new Query.FilterPredicate(Constants.IS_CANCELLED, Query.FilterOperator.EQUAL, true);
        final Query query = new Query(Constants.KIND_TRIP).setFilter(isCancelled);
        return datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
    }
}
