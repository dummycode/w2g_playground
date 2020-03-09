package edu.gatech.w2gplayground.Models.Generators;

import java.util.LinkedList;
import java.util.List;

import edu.gatech.w2gplayground.Models.Item;
import edu.gatech.w2gplayground.Models.Order;

public class OrderGenerator extends Generator {
    public static Order order() {
        List<Item> items = new LinkedList<>();

        // Add three items
        items.add(ItemGenerator.item());
        items.add(ItemGenerator.item());
        items.add(ItemGenerator.item());

        return new Order(items);
    }
}
