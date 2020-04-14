package edu.gatech.w2gplayground.Models.Generators;

import edu.gatech.w2gplayground.Models.Item;
import edu.gatech.w2gplayground.Models.Line;

public class LineGenerator extends Generator {
    public static Line line() {
        Item item = ItemGenerator.item();
        int quantity = 1;

        return new Line(item, quantity);
    }

    public static Line withQuantity(int quantity) {
        Line line = line();
        line.setQuantity(quantity);

        return line;
    }
}
