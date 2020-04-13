package edu.gatech.w2gplayground.Models.Generators;

import edu.gatech.w2gplayground.Models.Location;

public class LocationGenerator extends Generator {
    public static Location location() {
        String name = "LOC-01";
        String id = "725272730706";

        return new Location(name, id);
    }
}
