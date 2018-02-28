package com.dashk.reservation;

import com.dashk.reservation.models.input.Reservation;
import com.dashk.reservation.models.input.Table;
import com.dashk.reservation.models.output.ReservationAssignment;
import com.dashk.reservation.models.process.TableSchedule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ReservationAssigner {
    private static final Logger logger = LogManager.getLogger();

    public static List<ReservationAssignment> run(List<Table> tables, List<Reservation> reservations) {
        List<ReservationAssignment> reservationAssignments = createAssignments(reservations);
        List<TableSchedule> tableSchedules = createTableForAssignments(tables);
        sortTableScheduleByCapacity(tableSchedules);

        for (TableSchedule tableSchedule : tableSchedules) {
            logger.debug("Assigning reservations to %s", tableSchedule.getTable().getName());
            run(tableSchedule, reservationAssignments);
        }

        return reservationAssignments;
    }

    /**
     *
     *
     * @param tableSchedule
     * @param reservations
     */
    public static void run(TableSchedule tableSchedule, List<ReservationAssignment> reservations) {
        // Sorts reservation by party size, so we always assign larger parties first
        sortReservationAssignments(reservations);

        List<ReservationAssignment> schedulableReservations = filterAssignableReservationsByTable(
                tableSchedule, reservations
        );

        while (schedulableReservations.size() > 0) {
            ReservationAssignment schedulableReservationWithHighestCapacity = schedulableReservations.get(0);
            logger.debug(
                    String.format(
                            "Assigning %s to Table %s",
                            schedulableReservationWithHighestCapacity.getReservation().toString(),
                            tableSchedule.getTable().getName()
                    )
            );

            tableSchedule.schedule(schedulableReservationWithHighestCapacity);

            // Find the next set of schedulable reservations
            schedulableReservations = filterAssignableReservationsByTable(tableSchedule, reservations);
        }
    }

    /**
     * Finds list of reservations service-able by given table
     *
     * @param table
     * @param reservationAssignments
     * @return
     */
    private static List<ReservationAssignment> filterAssignableReservationsByTable(
            TableSchedule table, List<ReservationAssignment> reservationAssignments
    ) {
        List<ReservationAssignment> assignments = new ArrayList<ReservationAssignment>();

        for (ReservationAssignment reservationAssignment : reservationAssignments) {
            if (reservationAssignment.isAssigned()) {
                // Skip over if assignment is already reserved
            } else if (table.canHandle(reservationAssignment.getReservation())) {
                assignments.add(reservationAssignment);
            }
        }

        logger.debug(String.format(
                "# of remaining assignable reservations for table %s: %d", table.getTable().getName(), assignments.size()
                )
        );
        return assignments;
    }

    private static void sortReservationAssignments(List<ReservationAssignment> assignments) {
        Collections.sort(assignments, new Comparator<ReservationAssignment>() {
            public int compare(ReservationAssignment assignment1, ReservationAssignment assignment2) {
                int partySize1 = assignment1.getReservation().getPartySize();
                int partySize2 = assignment2.getReservation().getPartySize();

                if (partySize1 > partySize2) {
                    return -1;
                } else if (partySize1 < partySize2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    /**
     * Sort given list of table schedule in a descending order, with table with the
     * highest capacity in front
     *
     * @param tables
     */
    private static void sortTableScheduleByCapacity(List<TableSchedule> tables) {
        Collections.sort(tables, new Comparator<TableSchedule>() {
            /**
             * Table with higher capacity should be sorted BEFORE
             * the other.
             *
             * @param tableAssignment1
             * @param tableAssignment2
             * @return
             */
            public int compare(TableSchedule tableAssignment1, TableSchedule tableAssignment2) {
                Table table1 = tableAssignment1.getTable();
                Table table2 = tableAssignment2.getTable();

                return compareTableByCapacity(table1, table2);
            }
        });
    }

    /**
     * Compares table by capacity, with table with higher capacity as "smaller"
     *
     * @param table1
     * @param table2
     * @return
     */
    private static int compareTableByCapacity(Table table1, Table table2) {
        if (table1.getMaximum() > table2.getMaximum()) {
            return -1;
        } else if (table1.getMaximum() < table2.getMaximum()) {
            return 1;
        } else if (table1.getMinimum() > table2.getMinimum()) {
            return -1;
        } else if (table1.getMinimum() < table2.getMinimum()) {
            return 1;
        } else {
            return 0;
        }
    }

    private static List<ReservationAssignment> createAssignments(List<Reservation> reservations) {
        List<ReservationAssignment> assignments = new ArrayList<ReservationAssignment>();

        for (Reservation reservation : reservations) {
            assignments.add(
                    new ReservationAssignment(reservation)
            );
        }

        return assignments;
    }

    private static List<TableSchedule> createTableForAssignments(List<Table> tables) {
        List<TableSchedule> tableForAssignments = new ArrayList<TableSchedule>();

        for (Table table : tables) {
            tableForAssignments.add(new TableSchedule(table));
        }

        return tableForAssignments;
    }
}
