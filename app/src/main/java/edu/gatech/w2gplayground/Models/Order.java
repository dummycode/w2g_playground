package edu.gatech.w2gplayground.Models;

import java.util.List;

public class Order extends Model {
    List<Item> items;

    public Order(List<Item> items) {
        this.items = items;
    }

    public Item[] getItems() {
        return (Item []) this.items.toArray();
    }
}
