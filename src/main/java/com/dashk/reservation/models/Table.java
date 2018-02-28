package com.dashk.reservation.models;

import com.dashk.reservation.utils.ScheduleHelper;

public class Table {
    private String name;
    private int minimum;
    private int maximum;
    private boolean[] schedule;

    public Table(String name, int minimum, int maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Table's minimum capacity must be smaller than or equal to maximum");
        }

        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.schedule = ScheduleHelper.createEmptySchedule();
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

    /**
     * Returns true if this table can handle given reservation
     *
     * @param reservation
     * @return
     */
    public boolean canHandle(Reservation reservation) {
        // @NOTE: This does NOT check whether reservation is already assigned to a table or not.  It simply checks
        // whether this table, given its current capacity, could handle given reservation.

        // Make sure capacity matches
        if (reservation.getPartySize() < this.getMinimum() || reservation.getPartySize() > this.getMaximum()) {
            return false;
        }

        // Make sure restaurant still accepts new seating
        if (!ScheduleHelper.isSeatingAvailable(reservation.getStartTime())) {
            return false;
        }

        // Make sure assign is open
        int reservationStartTimeSlot = getScheduleStartTimeSlot(reservation);
        int reservationEndTimeSlot = getScheduleEndTimeSlot(reservation);
        for (int i = reservationStartTimeSlot; i <= reservationEndTimeSlot; ++i) {
            if (schedule[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the start time slot index in assign of given reservation
     * @param reservation
     * @return
     */
    private int getScheduleStartTimeSlot(Reservation reservation) {
        return ScheduleHelper.getScheduleIndexByTime(reservation.getStartTime());
    }

    /**
     * Returns the end time slot index in assign of given reservation
     *
     * @param reservation
     * @return
     */
    private int getScheduleEndTimeSlot(Reservation reservation) {
        return getScheduleStartTimeSlot(reservation) + ScheduleHelper.getOccupiedTimeslotByPartySize(reservation.getPartySize()) - 1;
    }

    /**
     * Assigns given reservation to this table
     *
     * @param reservation
     */
    public void assign(Reservation reservation) {
        if (reservation.isAssigned()) {
            throw new IllegalArgumentException(
                    String.format("Reservation has already been assigned to %s", reservation.getTable().getName())
            );
        } else if (!this.canHandle(reservation)) {
            throw new IllegalArgumentException(
                    String.format("Reservation cannot be handled by %s", reservation.getTable().getName())
            );
        }

        // Assign table to given reservationAssignment
        reservation.setTable(this);

        // Mark assign as occupied
        for (int i = getScheduleStartTimeSlot(reservation); i <= getScheduleEndTimeSlot(reservation); ++i) {
            this.schedule[i] = true;
        }
    }
}
