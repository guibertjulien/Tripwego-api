package com.tripwego.api;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.tripwego.api.query.RegionCounterQueries;
import com.tripwego.dto.RegionCounter;

import java.util.List;

@Api(name = "regioncounterendpoint", namespace = @ApiNamespace(ownerDomain = "bergui.com", ownerName = "bergui.com", packagePath = "tripwego.api"))
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
    @ApiMethod(name = "listRegionCounter")
    public CollectionResponse<RegionCounter> listRegionCounter(@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        final List<RegionCounter> regionCounters = regionCounterQueries.findAll();
        return CollectionResponse.<RegionCounter>builder().setItems(regionCounters).setNextPageToken(cursorString).build();
    }
}
