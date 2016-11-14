package com.example.tripwegoapi.dto.tripitem;

import com.example.tripwegoapi.dto.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JG on 12/11/16.
 */
public class Accommodation {

    private final List<Step> steps = new ArrayList<Step>();

    public List<Step> getSteps() {
        return steps;
    }
}
