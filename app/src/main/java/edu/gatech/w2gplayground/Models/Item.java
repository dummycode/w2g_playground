package edu.gatech.w2gplayground.Models;

public class Item extends Model {
    private String id;
    private String upc;
    private String name;

    public Item(String id, String upc, String name) {
        this.id = id;
        this.upc = upc;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
