package com.tripwego.api.user;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.tripwego.dto.common.Counter;

@Api(name = "userendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class UserEndpoint {

    private UserQueries queries = new UserQueries();

    @ApiMethod(name = "count", path = "count", httpMethod = ApiMethod.HttpMethod.GET)
    public Counter count() {
        final Counter counter = queries.count();
        return counter;
    }

}
