package com.dashk.reservation.models;

public class Reservation {
    private int startTime;
    private int count;
    private String id;
    private Table table;

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

    /**
     * Assigns reservation to given table
     *
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Returns true if this reservation has already been assigned.
     * @return
     */
    public boolean isAssigned() {
        return this.table != null;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public String toString() {
        String reservationName = String.format("Reservation %s (Size: %d), starting at %d", this.id, this.count, this.startTime);

        if (this.table == null) {
            return String.format("%s is UNASSIGNED", reservationName);
        } else {
            return String.format("%s is assigned to %s", reservationName, table.toString());
        }
    }
}
