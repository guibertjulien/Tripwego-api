package com.tripwego.api;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.tripwego.api.query.TagQueries;
import com.tripwego.domain.Tag;

import java.util.List;

@Api(name = "tagendpoint", namespace = @ApiNamespace(ownerDomain = "bergui.com", ownerName = "bergui.com", packagePath = "tripwego.api"))
public class TagEndpoint {

    private TagQueries tagQueries = new TagQueries();

    /**
     * This method lists all the entities inserted in datastore.
     * It uses HTTP GET method and paging support.
     *
     * @return A CollectionResponse class containing the list of all entities
     * persisted and a cursor to the next page.
     */
    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "listTag")
    public CollectionResponse<Tag> listTag(@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        final List<Tag> tags = tagQueries.findAll();
        return CollectionResponse.<Tag>builder().setItems(tags).setNextPageToken(cursorString).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "updateAllTags", path = "updateAllTags", httpMethod = ApiMethod.HttpMethod.GET)
    public void updateAllTags() {
        tagQueries.updateAllEntities();
    }

}
