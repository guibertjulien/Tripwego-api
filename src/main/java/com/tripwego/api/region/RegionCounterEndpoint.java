package com.tripwego.api.region;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.tripwego.dto.region.RegionCounter;

import java.util.List;

@Api(name = "regioncounterendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class RegionCounterEndpoint {

    private RegionCounterQueries regionCounterQueries = new RegionCounterQueries();

    /**
     * This method lists all the entities inserted in datastore.
     * It uses HTTP GET method and paging support.
     *
     * @return A CollectionResponse class containing the list of all entities
     * persisted and a cursor to the next page.
     */
    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllRegions", path = "findAllRegions", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<RegionCounter> findAllRegions(@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        final List<RegionCounter> regionCounters = regionCounterQueries.findAll();
        return CollectionResponse.<RegionCounter>builder().setItems(regionCounters).setNextPageToken(cursorString).build();
    }
}
