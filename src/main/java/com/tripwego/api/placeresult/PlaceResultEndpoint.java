package com.tripwego.api.placeresult;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.tripwego.dto.placeresult.PlaceResultDto;
import com.tripwego.dto.placeresult.PlaceResultDtoSearchCriteria;
import com.tripwego.dto.trip.Trip;

import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 19/02/17.
 */
@Api(name = "placeresultendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class PlaceResultEndpoint {

    private static final Logger LOGGER = Logger.getLogger(PlaceResultEndpoint.class.getName());

    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();
    private PlaceResultQueries queries = new PlaceResultQueries();

    @ApiMethod(name = "findPlaces", path = "findPlaces", httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<PlaceResultDto> findPlaces(PlaceResultDtoSearchCriteria criteria) {
        final List<PlaceResultDto> places = queries.findPlaces(criteria);
        return CollectionResponse.<PlaceResultDto>builder().setItems(places).build();
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