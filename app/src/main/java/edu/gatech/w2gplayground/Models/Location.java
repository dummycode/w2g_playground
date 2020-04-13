package edu.gatech.w2gplayground.Models;

import java.io.Serializable;
import java.util.List;

/**
 * Location model to represent a location in the warehouse
 */
public class Location extends Model implements Serializable {
    private String name;
    private String id;

    public Location(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
}
