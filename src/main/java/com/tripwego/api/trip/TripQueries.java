package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.tripwego.api.document.DocumentService;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.api.step.StepDtoMapper;
import com.tripwego.api.step.StepDtoMapperFactory;
import com.tripwego.api.user.UserQueries;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripSearchCriteria;
import com.tripwego.dto.user.MyUser;
import com.tripwego.dto.user.Traveler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.Query.*;
import static com.tripwego.api.ConfigurationConstants.LIMIT_QUERY_TRIP;
import static com.tripwego.api.ConfigurationConstants.LIMIT_TRIP_DOCUMENT_TO_STORE_BY_DAY;
import static com.tripwego.api.Constants.*;


/**
 * Created by JG on 04/06/16.
 */
public class TripQueries {

    private static final Logger LOGGER = Logger.getLogger(TripQueries.class.getName());

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
                    tripsFoundByText.add(trip.get());
                }
            }
        }
        // tags criteria
        if (criteria.getTripTags().size() > 0 || criteria.getCountries().size() > 0) {
            final List<Filter> tripTags = extractFilterList(criteria.getTripTags(), TAGS);
            final List<Filter> countryTags = extractFilterList(criteria.getCountries(), COUNTRY_CODE);
            // CAUTION "AND"
            final Filter filterTripTags = (tripTags.size() > 1) ? CompositeFilterOperator.and(tripTags) : (tripTags.size() == 1) ? tripTags.get(0) : null;
            // CAUTION "OR"
            final Filter filterCountryTags = (countryTags.size() > 1) ? CompositeFilterOperator.or(countryTags) : (countryTags.size() == 1) ? countryTags.get(0) : null;
            Filter filterResult;
            if (!tripTags.isEmpty() && !countryTags.isEmpty()) {
                filterResult = CompositeFilterOperator.and(filterTripTags, filterCountryTags);
            } else if (!tripTags.isEmpty()) {
                filterResult = filterTripTags;
            } else {
                filterResult = filterCountryTags;
            }
            final Query query = new Query(KIND_TRIP).setFilter(filterResult);
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
        //.addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class))
        //.addProjection(new PropertyProjection(CREATED_AT, String.class));
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelled() {
        final Filter isCancelled = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Query query = new Query(KIND_TRIP).setFilter(isCancelled);
        //.addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class));
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> findTripEntitiesCancelledByUser(String userId) {
        final Filter byUser = new FilterPredicate(USER_ID, FilterOperator.EQUAL, userId);
        final Filter isCancelled = new FilterPredicate(IS_CANCELLED, FilterOperator.EQUAL, true);
        final Filter filters = CompositeFilterOperator.and(byUser, isCancelled);
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
        Optional<Trip> result = Optional.absent();
        try {
            final Entity entity = datastore.get(KeyFactory.stringToKey(id));
            result = Optional.of(tripDtoMapper.map(entity, retrieveUser(entity)));
        } catch (EntityNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
        return result;
    }

    private Optional<MyUser> retrieveUser(Entity entity) {
        Optional<MyUser> user = Optional.absent();
        if (entity.getProperty(USER_ID) != null) {
            final String userId = String.valueOf(entity.getProperty(USER_ID));
            user = userQueries.findByUserId(userId);
        }
        return user;
    }
}
