package edu.gatech.w2gplayground.Models;

import java.util.List;

public class Order extends Model {
    private List<Line> lines;

    public Order(List<Line> lines) {
        this.lines = lines;
    }

    public Line[] getLines() {
        return (Line []) this.lines.toArray();
    }
}
