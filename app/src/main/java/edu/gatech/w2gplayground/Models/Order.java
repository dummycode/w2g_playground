package edu.gatech.w2gplayground.Models;

import java.util.List;

public class Order extends Model {
    private String id;
    private List<Line> lines;

    public Order(String id, List<Line> lines) {
        this.id = id;
        this.lines = lines;
    }

    public Line[] getLines() {
        return (Line []) this.lines.toArray();
    }

    public String getId() {
        return id;
    }

    public int getQuantity() {
        int total = 0;

        for (Line line: lines) {
           total += line.getQuantity();
        }

        return total;
    }
}
