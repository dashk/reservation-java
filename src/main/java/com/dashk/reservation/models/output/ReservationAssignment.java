package com.dashk.reservation.models.output;

import com.dashk.reservation.models.input.Reservation;
import com.dashk.reservation.models.input.Table;

public class ReservationAssignment {
    private Table table;
    private Reservation reservation;

    public ReservationAssignment(
            Reservation reservation
    ) {
        this.table = null;
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
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

    @Override
    public String toString() {
        if (this.table == null) {
            return String.format("%s is not assigned to any table", reservation.toString());
        } else {
            return String.format("%s is assigned to %s", reservation.toString(), table.toString());
        }
    }

    public Table getTable() {
        return table;
    }
}
