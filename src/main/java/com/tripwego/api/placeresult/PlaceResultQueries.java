package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.GeoPtEntityMapper;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.placeresult.PlaceResultDtoSearchCriteria;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.appengine.api.datastore.Query.*;
import static com.google.appengine.api.datastore.Query.FilterOperator.*;
import static com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;
import static com.tripwego.api.ConfigurationConstants.*;
import static com.tripwego.api.Constants.*;
import static com.tripwego.api.step.StepCategory.*;

/**
 * Created by JG on 19/02/17.
 */
public class PlaceResultQueries {

    private static final List<String> IS_ACTIVITY = Arrays.asList(
            ACTIVITY.name(),
            FOOD.name(),
            NIGHT_LIFE.name(),
            SHOPPING.name());
    private static final String COUNTRY = "country";

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private PlaceResultDtoMapper placeResultDtoMapper = new PlaceResultDtoMapperFactory().create();

    private GeoPtEntityMapper geoPtEntityMapper = new GeoPtEntityMapper();

    public List<PlaceResultDto> findDestinationSuggestions(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> result = new ArrayList<>();
        final Filter byStepCategory = new FilterPredicate(STEP_CATEGORIES, EQUAL, DESTINATION.name());
        //final Filter byType = new FilterPredicate(TYPES, EQUAL, COUNTRY);
        final Query query = new Query(KIND_PLACE).setFilter(byStepCategory);
        //final Query query = new Query(KIND_PLACE).setFilter(byStepCategory);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        Collections.shuffle(entities);
        for (Entity entity : entities.size() > LIMIT_DESTINATION_SUGGESTION ? entities.subList(0, LIMIT_DESTINATION_SUGGESTION) : entities) {
            final PlaceResultDto placeResultDto = placeResultDtoMapper.map(entity, criteria.getLanguage());
            result.add(placeResultDto);
        }
        return result;
    }

    public List<PlaceResultDto> findPlaces(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> result = new ArrayList<>();
        final List<Entity> entities = findPlaceEntities(criteria);
        for (Entity entity : entities) {
            final PlaceResultDto placeResultDto = placeResultDtoMapper.map(entity, criteria.getLanguage());
            result.add(placeResultDto);
        }
        return result;
    }

    /**
     * properties_used_in_inequality_filters_must_be_sorted_first
     * inequality_filters_are_limited_to_at_most_one_property
     *
     * @param criteria
     * @return
     */
    public List<Entity> findPlaceEntities(PlaceResultDtoSearchCriteria criteria) {
        List<Entity> result = new ArrayList<>();
        Filter byStepCategoryOrSuggestionType = null;
        if (criteria.getStepCategories() != null && !criteria.getStepCategories().isEmpty()) {
            final String first = criteria.getStepCategories().get(0);
            if (ACTIVITY.name().equals(first)) {
                byStepCategoryOrSuggestionType = new FilterPredicate(STEP_CATEGORIES, FilterOperator.IN, PlaceResultQueries.IS_ACTIVITY);
            } else {
                byStepCategoryOrSuggestionType = new FilterPredicate(STEP_CATEGORIES, EQUAL, first);
            }
        }
        if (criteria.getSuggestionTypes() != null && !criteria.getSuggestionTypes().isEmpty()) {
            final String first = criteria.getSuggestionTypes().get(0);
            byStepCategoryOrSuggestionType = new FilterPredicate(SUGGESTION_TYPES, EQUAL, first);
        }
        if (criteria.getCountryCode() != null) {
            final Filter byCountry = new FilterPredicate(COUNTRY_CODE, EQUAL, criteria.getCountryCode());
            final CompositeFilter compositeFilter = CompositeFilterOperator.and(byStepCategoryOrSuggestionType, byCountry);
            final Query query = new Query(KIND_PLACE).setFilter(compositeFilter)
                    .addSort(POPULATION, DESCENDING);
            final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT_PLACES));
            result.addAll(entities);
        }
        if (criteria.getBounds() != null) {
            if (GEO_SPATIAL_SEARCH_TURN_ON) {
                final StContainsFilter stContainsFilter = extractBoundsFilter(criteria);
                final Query query = new Query(KIND_PLACE).setFilter(stContainsFilter);
                final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
                result.addAll(entities);
            } else {
                final CompositeFilter latFilter = extractLatFilterWorkaround(criteria);
                final CompositeFilter compositeFilterLat = CompositeFilterOperator.and(byStepCategoryOrSuggestionType, latFilter);
                final Query latQuery = new Query(KIND_PLACE).setFilter(compositeFilterLat);
                final CompositeFilter lngFilter = extractLngFilterWorkaround(criteria);
                final CompositeFilter compositeFilterLng = CompositeFilterOperator.and(byStepCategoryOrSuggestionType, lngFilter);
                final Query lngQuery = new Query(KIND_PLACE).setFilter(compositeFilterLng);
                final List<Entity> entitiesWithLat = datastore.prepare(latQuery).asList(FetchOptions.Builder.withDefaults());
                final List<Entity> entitiesWithLng = datastore.prepare(lngQuery).asList(FetchOptions.Builder.withDefaults());
                result.addAll(entitiesWithLat);
                result.retainAll(entitiesWithLng);
            }
            Comparator<Entity> byRatingThenCounter = Comparator
                    .comparing((Function<Entity, Double>) p -> (Double) p.getProperty(RATING))
                    .thenComparing(p -> (Long) p.getProperty(COUNTER));
            Collections.sort(result, Collections.reverseOrder(byRatingThenCounter));
            if (result.size() > LIMIT_PLACES) {
                result = result.subList(0, LIMIT_PLACES);
            }
        }
        return result;
    }

    // TODO remove workaround
    private CompositeFilter extractLatFilterWorkaround(PlaceResultDtoSearchCriteria criteria) {
        final Filter latMinFilter =
                new FilterPredicate(LATITUDE, GREATER_THAN_OR_EQUAL, criteria.getBounds().getSouthWest().getLatitude());
        final Filter latMaxFilter =
                new FilterPredicate(LATITUDE, LESS_THAN_OR_EQUAL, criteria.getBounds().getNorthEast().getLatitude());
        // Use CompositeFilter to combine multiple filters
        return CompositeFilterOperator.and(latMinFilter, latMaxFilter);
    }

    // TODO remove workaround
    private CompositeFilter extractLngFilterWorkaround(PlaceResultDtoSearchCriteria criteria) {
        final Filter lngMinFilter =
                new FilterPredicate(LONGITUDE, GREATER_THAN_OR_EQUAL, criteria.getBounds().getSouthWest().getLongitude());
        final Filter lngMaxFilter =
                new FilterPredicate(LONGITUDE, LESS_THAN_OR_EQUAL, criteria.getBounds().getNorthEast().getLongitude());
        // Use CompositeFilter to combine multiple filters
        return CompositeFilterOperator.and(lngMinFilter, lngMaxFilter);
    }

    // TODO replace workaround when GAE version is OK for geo search
    private StContainsFilter extractBoundsFilter(PlaceResultDtoSearchCriteria criteria) {
        final GeoPt southwest = geoPtEntityMapper.map(criteria.getBounds().getSouthWest());
        final GeoPt northeast = geoPtEntityMapper.map(criteria.getBounds().getNorthEast());
        return new StContainsFilter(CENTER_PT, new GeoRegion.Rectangle(southwest, northeast));
    }

    // TODO fix
    public List<Entity> findPlaceEntitiesUnusedWithCounter() {
        final Filter hasCounterAtZero = new FilterPredicate(COUNTER, FilterOperator.EQUAL, 0);
        final Query query = new Query(KIND_PLACE).setFilter(hasCounterAtZero);
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Key> findPlaceEntitiesUnused() {
        Set<String> placeKeysUsed = new HashSet<>();
        // list of place with step parent
        final Query queryStep = new Query(KIND_STEP).addProjection(new PropertyProjection(PLACE_KEY, String.class));
        final List<Entity> stepEntities = datastore.prepare(queryStep).asList(FetchOptions.Builder.withDefaults());
        for (Entity stepEntity : stepEntities) {
            placeKeysUsed.add(String.valueOf(stepEntity.getProperty(PLACE_KEY)));
        }
        // list of place with trip parent
        final Query queryTrip = new Query(KIND_TRIP).addProjection(new PropertyProjection(PLACE_KEY, String.class));
        final List<Entity> tripEntities = datastore.prepare(queryTrip).asList(FetchOptions.Builder.withDefaults());
        for (Entity stepEntity : tripEntities) {
            placeKeysUsed.add(String.valueOf(stepEntity.getProperty(PLACE_KEY)));
        }
        // list of places
        final List<Entity> placeKeys = datastore.prepare(new Query(KIND_PLACE).setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        List<Key> keys = placeKeys.stream().map(p -> p.getKey()).collect(Collectors.toList());
        // extract all places unused
        List<Key> keysUsed = placeKeysUsed.stream().map(p -> KeyFactory.stringToKey(p)).collect(Collectors.toList());
        keys.removeAll(keysUsed);
        return keys;
    }
}
