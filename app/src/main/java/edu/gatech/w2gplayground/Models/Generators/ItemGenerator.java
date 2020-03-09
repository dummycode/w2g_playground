package edu.gatech.w2gplayground.Models.Generators;

import edu.gatech.w2gplayground.Models.Item;

public class ItemGenerator extends Generator {
    public static Item item() {
        return new Item(randId(), randUpc(), "My Item");
    }
}
