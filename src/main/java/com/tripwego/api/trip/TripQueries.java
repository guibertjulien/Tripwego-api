package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.tripwego.api.common.Strings;
import com.tripwego.api.document.DocumentService;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.api.step.StepDtoMapper;
import com.tripwego.api.step.StepDtoMapperFactory;
import com.tripwego.api.trip.status.TripAdminStatus;
import com.tripwego.api.user.UserQueries;
import com.tripwego.dto.common.Counter;
import com.tripwego.dto.statistics.Statistics;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripSearchCriteria;
import com.tripwego.dto.user.MyUser;
import com.tripwego.dto.user.Traveler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.Query.*;
import static com.tripwego.api.ConfigurationConstants.LIMIT_QUERY_TRIP;
import static com.tripwego.api.ConfigurationConstants.LIMIT_TRIP_DOCUMENT_TO_STORE_BY_DAY;
import static com.tripwego.api.Constants.*;
import static com.tripwego.api.trip.status.TripAdminStatus.*;
import static com.tripwego.api.trip.status.TripUserStatus.PUBLISHED;
import static com.tripwego.api.trip.status.TripVisibility.PUBLIC;


/**
 * Created by JG on 04/06/16.
 */
public class TripQueries {

    private static final Logger LOGGER = Logger.getLogger(TripQueries.class.getName());
    public static final List<String> ADMIN_STATUS_VISIBLE = Arrays.asList(UPDATED.name(), FORKED.name(), CHECKED.name());
    public static final List<String> ADMIN_STATUS_SEO_VISIBLE = Arrays.asList(CHECKED.name());

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private TripDtoMapper tripDtoMapper = new TripDtoMapperFactory().create();
    private StepDtoMapper stepDtoMapper = new StepDtoMapperFactory().create();
    private UserQueries userQueries = new UserQueries();
    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();
    private DocumentService documentService = new DocumentService();

    public List<Trip> findTripsByUser(String userId) {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
        final Filter isAdminStatusVisible = new FilterPredicate(TRIP_ADMIN_STATUS, FilterOperator.IN, ADMIN_STATUS_VISIBLE);
        final Filter filters = CompositeFilterOperator.and(byUser, isAdminStatusVisible);
        // query
        final Query query = new Query(KIND_TRIP).setFilter(filters).addSort(CREATED_AT, SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final Trip trip = tripDtoMapper.map(entity, Optional.<MyUser>empty());
            result.add(trip);
        }
        return result;
    }

    public List<Trip> findAllTripsAlive(Integer offset, Integer limit) {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Filter notCancelledByUser = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, false);
        final Filter isPublished = new FilterPredicate(TRIP_USER_STATUS, FilterOperator.EQUAL, PUBLISHED.name());
        final Filter isPublic = new FilterPredicate(TRIP_VISIBILITY, FilterOperator.EQUAL, PUBLIC.name());
        final Filter isAdminStatusVisible = new FilterPredicate(TRIP_ADMIN_STATUS, FilterOperator.IN, ADMIN_STATUS_VISIBLE);
        final Filter filters = CompositeFilterOperator.and(notCancelledByUser, isPublished, isPublic, isAdminStatusVisible);
        // query
        final Query query = new Query(KIND_TRIP).setFilter(filters).addSort(CREATED_AT, SortDirection.DESCENDING);
        List<Entity> entities;
        if (limit > 0) {
            entities = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(offset).limit(limit));
        } else {
            entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        }
        for (Entity entity : entities) {
            final Trip trip = tripDtoMapper.map(entity, retrieveUser(entity));
            result.add(trip);
        }
        return result;
    }

    // TODO add projections to optimize ?
    public List<Trip> findAllTripsForSeo() {
        final List<Trip> result = new ArrayList<>();
        // filters
        final Filter notCancelledByUser = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, false);
        final Filter isPublished = new FilterPredicate(TRIP_USER_STATUS, FilterOperator.EQUAL, PUBLISHED.name());
        final Filter isPublic = new FilterPredicate(TRIP_VISIBILITY, FilterOperator.EQUAL, PUBLIC.name());
        final Filter isAdminStatusVisible = new FilterPredicate(TRIP_ADMIN_STATUS, FilterOperator.IN, ADMIN_STATUS_SEO_VISIBLE);
        final Filter filters = CompositeFilterOperator.and(notCancelledByUser, isPublished, isPublic, isAdminStatusVisible);
        // query
        final Query query = new Query(KIND_TRIP).setFilter(filters).addSort(CREATED_AT, SortDirection.DESCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final Trip trip = tripDtoMapper.map(entity, retrieveUser(entity));
            result.add(trip);
        }
        return result;
    }

    public List<Trip> findAllTripsForAdmin(Integer offset, Integer limit, List<String> categoryNames) {
        final List<Trip> result = new ArrayList<>();
        final Query query;
        /*
        if (asList("TRIP_AUTOMATIC", "TRIP_MANUAL").containsAll(categoryNames)) {
            final Filter isTripAutomatic = new FilterPredicate(IS_TRIP_AUTOMATIC, FilterOperator.EQUAL, asList("TRIP_AUTOMATIC").containsAll(categoryNames));
            query = new Query(KIND_TRIP).setFilter(isTripAutomatic).addSort(CREATED_AT, SortDirection.DESCENDING);
        }
        */
        query = new Query(KIND_TRIP).addSort(CREATED_AT, SortDirection.DESCENDING);
        // query
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(offset).limit(limit));
        for (Entity entity : entities) {
            final Trip trip = tripDtoMapper.map(entity, retrieveUser(entity));
            result.add(trip);
        }
        return result;
    }

    public List<Trip> find(TripSearchCriteria criteria) {
        final List<Trip> results = new ArrayList<>();
        final List<Trip> tripsFoundByText = new ArrayList<>();
        final List<Trip> tripsFoundByTags = new ArrayList<>();
        // text criteria
        if (!Strings.isNullOrEmpty(criteria.getText())) {
            Results<ScoredDocument> scoredDocuments = documentService.searchTripByText(criteria.getText());
            for (ScoredDocument scoredDocument : scoredDocuments) {
                final String scoredDocumentId = scoredDocument.getId();
                final Optional<Trip> trip = retrieveLazyTripAndUser(scoredDocumentId);
                if (trip.isPresent()) {
                    if (!trip.get().isCancelled()
                            && PUBLISHED.name().equals(trip.get().getTripUserStatus())
                            && PUBLIC.name().equals(trip.get().getTripVisibility())
                            && ADMIN_STATUS_VISIBLE.contains(trip.get().getTripAdminStatus())) {
                        tripsFoundByText.add(trip.get());
                    }
                }
            }
        }
        // tags criteria
        if (criteria.getTripTags().size() > 0 || criteria.getCountries().size() > 0) {
            final Filter isPublished = new FilterPredicate(TRIP_USER_STATUS, FilterOperator.EQUAL, PUBLISHED.name());
            final Filter isPublic = new FilterPredicate(TRIP_VISIBILITY, FilterOperator.EQUAL, PUBLIC.name());
            final Filter isAdminStatusVisible = new FilterPredicate(TRIP_ADMIN_STATUS, FilterOperator.IN, ADMIN_STATUS_VISIBLE);
            final List<Filter> tripTags = extractFilterList(criteria.getTripTags(), TAGS);
            final List<Filter> countryTags = extractFilterList(criteria.getCountries(), COUNTRY_CODE);
            // CAUTION "AND"
            final Filter filterTripTags = (tripTags.size() > 1) ? CompositeFilterOperator.and(tripTags) : (tripTags.size() == 1) ? tripTags.get(0) : null;
            // CAUTION "OR"
            final Filter filterCountryTags = (countryTags.size() > 1) ? CompositeFilterOperator.or(countryTags) : (countryTags.size() == 1) ? countryTags.get(0) : null;
            Filter filterTags;
            if (!tripTags.isEmpty() && !countryTags.isEmpty()) {
                filterTags = CompositeFilterOperator.and(filterTripTags, filterCountryTags);
            } else if (!tripTags.isEmpty()) {
                filterTags = filterTripTags;
            } else {
                filterTags = filterCountryTags;
            }
            final Filter filters = CompositeFilterOperator.and(isPublished, isPublic, isAdminStatusVisible, filterTags);
            final Query query = new Query(KIND_TRIP).setFilter(filters);
            final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT_QUERY_TRIP));
            for (Entity entity : entities) {
                final Trip trip = tripDtoMapper.map(entity, retrieveUser(entity));
                tripsFoundByTags.add(trip);
            }
        }
        if (!tripsFoundByText.isEmpty()) {
            if (!tripsFoundByTags.isEmpty()) {
                // intersection
                results.addAll(tripsFoundByText);
                results.retainAll(tripsFoundByTags);
            } else if (criteria.getTripTags().isEmpty() && criteria.getCountries().isEmpty()) {
                // just text
                results.addAll(tripsFoundByText);
            }
        } else if (Strings.isNullOrEmpty(criteria.getText())) {
            // just tags
            results.addAll(tripsFoundByTags);
        }
        // duration filter
        final List<Trip> resultWithDuration = new ArrayList<>();
        for (Trip trip : results) {
            if (trip.getDuration() >= criteria.getMinDuration() && trip.getDuration() <= criteria.getMaxDuration()) {
                resultWithDuration.add(trip);
            }
        }
        return resultWithDuration;
    }

    // TODO optimize with IN (limit 30) for OR operator ?
    private List<Filter> extractFilterList(List<String> tags, String propertyName) {
        final List<Filter> result = new ArrayList<>();
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                result.add(new FilterPredicate(propertyName, FilterOperator.EQUAL, tag));
            }
        }
        return result;
    }

    public List<Step> findStepsByTripItem(Entity parent) {
        final List<Step> result = new ArrayList<>();
        final List<Entity> entities = findStepEntitiesKeyByTripItem(parent);
        for (Entity stepEntity : entities) {
            final Step step = stepDtoMapper.map(stepEntity);
            step.setPlaceResultDto(placeResultRepository.retrieve(step.getPlaceResultId()));
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
        //.addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class))
        //.addProjection(new PropertyProjection(CREATED_AT, String.class));
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelled() {
        final Filter isCancelledByUser = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Filter isCancelledByAdmin = new FilterPredicate(TRIP_ADMIN_STATUS, FilterOperator.EQUAL, TripAdminStatus.CANCELLED.name());
        final Query query = new Query(KIND_TRIP).setFilter(CompositeFilterOperator.or(isCancelledByUser, isCancelledByAdmin));
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelledByUser(String userId) {
        final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
        final Filter isCancelledByUser = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Filter filters = CompositeFilterOperator.and(byUser, isCancelledByUser);
        final Query query = new Query(KIND_TRIP).setFilter(filters).addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class));
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    // CAUTION : NO Projections
    public List<Entity> findTripEntitiesToStoreInDocument() {
        final Filter notStoredInDocument = new FilterPredicate(IS_STORE_IN_DOCUMENT, FilterOperator.EQUAL, false);
        final Query query = new Query(KIND_TRIP).setFilter(notStoredInDocument);
        return datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT_TRIP_DOCUMENT_TO_STORE_BY_DAY));
    }

    public Optional<Trip> retrieveLazyTripAndUser(String id) {
        Optional<Trip> result = Optional.empty();
        try {
            final Entity entity = datastore.get(KeyFactory.stringToKey(id));
            result = Optional.of(tripDtoMapper.map(entity, retrieveUser(entity)));
        } catch (EntityNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
        return result;
    }

    private Optional<MyUser> retrieveUser(Entity entity) {
        Optional<MyUser> user = Optional.empty();
        if (entity.getProperty(USER_ID) != null) {
            final String userId = String.valueOf(entity.getProperty(USER_ID));
            user = userQueries.findByUserId(userId);
        }
        return user;
    }

    public Statistics statistics() {
        final Statistics statistics = new Statistics();
        statistics.setTripCounter(countKind(KIND_TRIP));
        statistics.setUserCounter(countKind(KIND_USER));
        return statistics;
    }

    private Long countKind(String kind) {
        final Query query = new Query("__Stat_Kind__");
        final Filter filter = new FilterPredicate("kind_name", FilterOperator.EQUAL, kind);
        query.setFilter(filter);
        final Entity entityStat = datastore.prepare(query).asSingleEntity();
        return (Long) entityStat.getProperty("count");
    }

    public Counter count() {
        final Counter counter = new Counter();
        final Query query = new Query(KIND_TRIP).addSort(CREATED_AT, SortDirection.DESCENDING).setKeysOnly();
        counter.setCount(Long.valueOf(datastore.prepare(query).asList(FetchOptions.Builder.withDefaults()).size()));
        return counter;
    }
}
