package com.dashk.reservation;

import com.dashk.reservation.models.Reservation;
import com.dashk.reservation.models.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReservationAssigner {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Assigns given reservations to given list of tables
     *
     * @param tables
     * @param reservations
     * @return
     */
    public static void run(List<Table> tables, List<Reservation> reservations) {
        sortTableByCapacity(tables);

        for (Table table : tables) {
            logger.debug("Assigning reservations to %s", table.getName());
            run(table, reservations);
        }
    }

    /**
     * Assigns given reservations to given table
     *
     * @param table
     * @param reservations
     */
    public static void run(Table table, List<Reservation> reservations) {
        // Sorts reservation by party size, so we always assign larger parties first
        sortReservationByPartySize(reservations);

        List<Reservation> availableReservations = getAvailableReservationsByTable(table, reservations);

        while (availableReservations.size() > 0) {
            Reservation schedulableReservationWithHighestCapacity = availableReservations.get(0);
            logger.debug(
                    String.format(
                            "Assigning %s to Table %s",
                            schedulableReservationWithHighestCapacity.toString(),
                            table.getName()
                    )
            );

            table.assign(schedulableReservationWithHighestCapacity);

            // Find the next set of schedulable reservations
            availableReservations = getAvailableReservationsByTable(table, reservations);
        }
    }

    /**
     * Finds list of reservations service-able by given table
     *
     * @param table
     * @param reservations
     * @return
     */
    private static List<Reservation> getAvailableReservationsByTable(
            Table table, List<Reservation> reservations
    ) {
        List<Reservation> availableReservations = new ArrayList<Reservation>();

        for (Reservation reservation : reservations) {
            if (!reservation.isAssigned() && table.canHandle(reservation)) {
                availableReservations.add(reservation);
            }
        }

        logger.debug(String.format(
                "# of remaining assignable reservations for table %s: %d", table.getName(), availableReservations.size()
                )
        );
        return availableReservations;
    }

    private static void sortReservationByPartySize(List<Reservation> assignments) {
        Collections.sort(assignments, new Comparator<Reservation>() {
            public int compare(Reservation assignment1, Reservation assignment2) {
                int partySize1 = assignment1.getPartySize();
                int partySize2 = assignment2.getPartySize();

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
     * Sort given list of table assign in a descending order, with table with the
     * highest capacity in front
     *
     * @param tables
     */
    private static void sortTableByCapacity(List<Table> tables) {
        Collections.sort(tables, new Comparator<Table>() {
            /**
             * Table with higher capacity should be sorted BEFORE the other.
             *
             * @param table1
             * @param table2
             * @return
             */
            public int compare(Table table1, Table table2) {
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

}
