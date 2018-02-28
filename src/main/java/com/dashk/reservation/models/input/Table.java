package com.dashk.reservation.models.input;

public class Table {
    private String name;
    private int minimum;
    private int maximum;

    public Table(String name, int minimum, int maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Table's minimum capacity must be smaller than or equal to maximum");
        }

        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public String getName() {
        return name;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    @Override
    public String toString() {
        return String.format("%s (Capacity: %d - %d)", this.name, this.minimum, this.maximum);
    }
}
