package edu.gatech.w2gplayground.Models;

public class Item extends Model {
    String id;
    String upc;
    String name;

    public Item(String id, String upc, String name) {
        this.id = id;
        this.upc = upc;
        this.name = name;
    }
}
