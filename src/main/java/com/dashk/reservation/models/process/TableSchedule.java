package com.dashk.reservation.models.process;

import com.dashk.reservation.models.input.Reservation;
import com.dashk.reservation.models.input.Table;
import com.dashk.reservation.models.output.ReservationAssignment;
import com.dashk.reservation.utils.ScheduleHelper;

public class TableSchedule {
    private Table table;
    private boolean[] schedule;

    public TableSchedule(Table table) {
        this.table = table;
        this.schedule = ScheduleHelper.createEmptySchedule();
    }

    public Table getTable() {
        return table;
    }

    /**
     * Returns true if this table can handle given reservation
     *
     * @param reservation
     * @return
     */
    public boolean canHandle(Reservation reservation) {
        // Make sure capacity matches
        if (reservation.getPartySize() < this.table.getMinimum() || reservation.getPartySize() > this.table.getMaximum()) {
            return false;
        }

        // Make sure restaurant still accepts new seating
        if (!ScheduleHelper.isSeatingAvailable(reservation.getStartTime())) {
            return false;
        }

        // Make sure schedule is open
        int reservationStartTimeSlot = getScheduleStartTimeSlot(reservation);
        int reservationEndTimeSlot = getScheduleEndTimeSlot(reservation);
        for (int i = reservationStartTimeSlot; i <= reservationEndTimeSlot; ++i) {
            if (schedule[i]) {
                return false;
            }
        }

        return true;
    }

    private int getScheduleStartTimeSlot(Reservation reservation) {
        return ScheduleHelper.getScheduleIndexByTime(reservation.getStartTime());
    }

    private int getScheduleEndTimeSlot(Reservation reservation) {
        return getScheduleStartTimeSlot(reservation) + ScheduleHelper.getOccupiedTimeslotByPartySize(reservation.getPartySize()) - 1;
    }

    /**
     * Assigns given reservation to thsi table
     *
     * @param reservationAssignment
     */
    public void schedule(ReservationAssignment reservationAssignment) {
        // Assign table to given reservationAssignment
        if (reservationAssignment.isAssigned()) {
            throw new IllegalArgumentException(
                    String.format("Reservation has already been assigned to %s", reservationAssignment.getTable().getName())
            );
        } else if (!this.canHandle(reservationAssignment.getReservation())) {
            throw new IllegalArgumentException(
                    String.format("Reservation cannot be handled by %s", reservationAssignment.getTable().getName())
            );
        }

        reservationAssignment.setTable(this.getTable());

        // Mark schedule as occupied
        Reservation reservation = reservationAssignment.getReservation();
        for (int i = getScheduleStartTimeSlot(reservation); i <= getScheduleEndTimeSlot(reservation); ++i) {
            this.schedule[i] = true;
        }
    }
}
