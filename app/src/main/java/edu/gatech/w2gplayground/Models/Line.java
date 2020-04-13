package edu.gatech.w2gplayground.Models;

import java.io.Serializable;

public class Line implements Serializable {
    private Item item;
    private int quantity;

    public Line(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Item getItem() {
        return item;
    }

    public String getId() {
        return "id";
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
