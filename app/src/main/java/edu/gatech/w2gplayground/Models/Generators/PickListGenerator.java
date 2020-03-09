package edu.gatech.w2gplayground.Models.Generators;

import java.util.LinkedList;
import java.util.List;

import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.Models.PickList;

public class PickListGenerator extends Generator {
    public static PickList pickList() {
        List<Order> orders = new LinkedList<>();

        // Add three items
        orders.add(OrderGenerator.order());
        orders.add(OrderGenerator.order());
        orders.add(OrderGenerator.order());

        return new PickList(randId(), orders);
    }
}
