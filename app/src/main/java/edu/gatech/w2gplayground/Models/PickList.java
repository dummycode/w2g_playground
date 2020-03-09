package edu.gatech.w2gplayground.Models;

import java.util.List;

public class PickList {
    private String id;
    private List<Order> orders;

    public PickList(String id, List<Order> orders) {
        this.id = id;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
