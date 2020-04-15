package edu.gatech.w2gplayground.Models.Generators;

import java.util.LinkedList;
import java.util.List;

import edu.gatech.w2gplayground.Models.Item;
import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.Models.Order;

public class OrderGenerator extends Generator {
    public static Order order() {
        List<Line> lines = new LinkedList<>();

        // Add three lines
        lines.add(LineGenerator.line());
        lines.add(LineGenerator.line());
        lines.add(LineGenerator.line());

        return new Order(randId(), lines);
    }

    public static Order withLines(int count) {
        List<Line> lines = new LinkedList<>();

        // Add three lines
        for (int i = 0; i < count; i++) {
            lines.add(LineGenerator.line());
        }

        return new Order(randId(), lines);
    }
}
