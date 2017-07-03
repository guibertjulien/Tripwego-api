package com.tripwego.api.placeresult;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.placeresult.PlaceResultDtoSearchCriteria;
import com.tripwego.dto.trip.Trip;

import java.util.List;

/**
 * Created by JG on 19/02/17.
 */
@Api(name = "placeresultendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class PlaceResultEndpoint {

    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();
    private PlaceResultQueries queries = new PlaceResultQueries();

    @ApiMethod(name = "find_destination_suggestions", path = "find_destination_suggestions", httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<PlaceResultDto> find_destination_suggestions(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> places = queries.findDestinationSuggestions();
        return CollectionResponse.<PlaceResultDto>builder().setItems(places).build();
    }

    @ApiMethod(name = "findPlaces", path = "findPlaces", httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<PlaceResultDto> findPlaces(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> places = queries.findPlaces(criteria);
        return CollectionResponse.<PlaceResultDto>builder().setItems(places).build();
    }

    @ApiMethod(name = "get_place_details", path = "get_place_details", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public PlaceResultDto get_place_details(@Named("id") String id) {
        return placeResultRepository.retrieveEager(id);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "updateCounter", path = "updateCounter", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateCounter(Trip trip) {
        placeResultRepository.updateCounterAndInitRating(trip);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "updateRating", path = "updateRating", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateRating(@Named("placeKey") String placeKey, @Named("userId") String userId, @Named("rating") Integer rating) {
        placeResultRepository.updateRating(placeKey, userId, rating);
    }
}