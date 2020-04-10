package edu.gatech.w2gplayground.Models;

import java.util.List;

/**
 * Location model to represent a location in the warehouse
 */
public class Location extends Model {
    private String locationId;

    public Location(String locationId) {
        this.locationId = locationId;
    }
}
