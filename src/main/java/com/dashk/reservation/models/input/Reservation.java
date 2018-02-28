package com.dashk.reservation.models.input;

public class Reservation {
    private int startTime;
    private int count;
    private String id;

    public Reservation(int startTime, int count) {
        this.startTime = startTime;
        this.count = count;

        // Generate a unique ID for display purposes
        this.id = String.format("%s", generateUniqueId());
    }

    public int getStartTime() {
        return startTime;
    }

    public int getPartySize() {
        return count;
    }

    private static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString().substring(0, 4);
    }

    @Override
    public String toString() {
        return String.format("Reservation %s (Size: %d), starting at %d", this.id, this.count, this.startTime);
    }
}
