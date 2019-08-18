package com.tripwego.api.step;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.tripwego.dto.step.Step;

/**
 * Created by JG on 19/02/17.
 */
@Api(name = "stependpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class StepEndpoint {

    private final StepRepository stepRepository = new StepRepository();

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "updateStepPhoto", path = "updateStepPhoto", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateStepPhoto(Step step) {
        stepRepository.updateStepPhoto(step);
    }
}