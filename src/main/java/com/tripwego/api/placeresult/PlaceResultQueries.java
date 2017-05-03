package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.common.mapper.GeoPtEntityMapper;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.placeresult.PlaceResultDtoSearchCriteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.appengine.api.datastore.Query.*;
import static com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;
import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 19/02/17.
 */
public class PlaceResultQueries {

    private static final int LIMIT_DESTINATION_SUGGESTION = 5;
    private static final int LIMIT_PLACES = 50;
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private PlaceResultDtoMapper placeResultDtoMapper = new PlaceResultDtoMapperFactory().create();

    private GeoPtEntityMapper geoPtEntityMapper = new GeoPtEntityMapper();

    // TODO enrich criteria
    public List<PlaceResultDto> findDestinationSuggestions(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> result = new ArrayList<>();
        final Filter byStepCategory = new FilterPredicate(STEP_CATEGORIES, FilterOperator.EQUAL, "DESTINATION");
        final Query query = new Query(KIND_PLACE_RESULT).setFilter(byStepCategory);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT_DESTINATION_SUGGESTION));
        for (Entity entity : entities) {
            final PlaceResultDto placeResultDto = placeResultDtoMapper.map(entity);
            result.add(placeResultDto);
        }
        return result;
    }

    public List<PlaceResultDto> findPlaces(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> result = new ArrayList<>();
        final List<Entity> entities = findPlaceEntities(criteria);
        for (Entity entity : entities) {
            final PlaceResultDto placeResultDto = placeResultDtoMapper.map(entity);
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
        final List<Entity> result = new ArrayList<>();
        final String firstStepCategory = criteria.getStepCategories().get(0);
        final Filter byStepCategory = new FilterPredicate(STEP_CATEGORIES, FilterOperator.EQUAL, firstStepCategory);
        if (criteria.getCountry() != null) {
            final Filter byCountry = new FilterPredicate(COUNTRY_CODE, FilterOperator.EQUAL, criteria.getCountry().getCode());
            final Query query = new Query(KIND_PLACE_RESULT).setFilter(CompositeFilterOperator.and(byStepCategory, byCountry))
                    .addSort(COUNTER, DESCENDING).addSort(RATING, DESCENDING);
            final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT_PLACES));
            result.addAll(entities);
        }
        if (criteria.getBounds() != null) {
            final CompositeFilter latFilter = extractLatFilterWorkaround(criteria);
            final Query latQuery = new Query(KIND_PLACE_RESULT).setFilter(CompositeFilterOperator.and(byStepCategory, latFilter));
            final CompositeFilter lngFilter = extractLngFilterWorkaround(criteria);
            final Query lngQuery = new Query(KIND_PLACE_RESULT).setFilter(CompositeFilterOperator.and(byStepCategory, lngFilter));
            final List<Entity> entitiesWithLat = datastore.prepare(latQuery).asList(FetchOptions.Builder.withDefaults());
            final List<Entity> entitiesWithLng = datastore.prepare(lngQuery).asList(FetchOptions.Builder.withDefaults());
            result.addAll(entitiesWithLat);
            result.retainAll(entitiesWithLng);
            // sorting by COUNTER and RATING in JAVA
            Collections.sort(result, new PlaceComparator());
        }
        return result;
    }

    // TODO remove workaround
    private CompositeFilter extractLatFilterWorkaround(PlaceResultDtoSearchCriteria criteria) {
        final Filter latMinFilter =
                new FilterPredicate(LATITUDE, FilterOperator.GREATER_THAN_OR_EQUAL, criteria.getBounds().getSouthWest().getLatitude());
        final Filter latMaxFilter =
                new FilterPredicate(LATITUDE, FilterOperator.LESS_THAN_OR_EQUAL, criteria.getBounds().getNorthEast().getLatitude());
        // Use CompositeFilter to combine multiple filters
        return CompositeFilterOperator.and(latMinFilter, latMaxFilter);
    }

    // TODO remove workaround
    private CompositeFilter extractLngFilterWorkaround(PlaceResultDtoSearchCriteria criteria) {
        final Filter lngMinFilter =
                new FilterPredicate(LONGITUDE, FilterOperator.GREATER_THAN_OR_EQUAL, criteria.getBounds().getSouthWest().getLongitude());
        final Filter lngMaxFilter =
                new FilterPredicate(LONGITUDE, FilterOperator.LESS_THAN_OR_EQUAL, criteria.getBounds().getNorthEast().getLongitude());
        // Use CompositeFilter to combine multiple filters
        return CompositeFilterOperator.and(lngMinFilter, lngMaxFilter);
    }

    // TODO replace workaround when GAE version is OK for geo search
    private StContainsFilter extractBoundsFilter(PlaceResultDtoSearchCriteria criteria) {
        final GeoPt southwest = geoPtEntityMapper.map(criteria.getBounds().getSouthWest());
        final GeoPt northeast = geoPtEntityMapper.map(criteria.getBounds().getNorthEast());
        return new StContainsFilter(CENTER_PT, new GeoRegion.Rectangle(southwest, northeast));
    }
}