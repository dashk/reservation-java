package com.dashk.reservation;

import com.dashk.reservation.models.input.Reservation;
import com.dashk.reservation.models.input.Table;
import com.dashk.reservation.models.output.ReservationAssignment;
import com.dashk.reservation.utils.CsvFileReader;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationRunner {
    private static final Logger logger = LogManager.getLogger();
    private static final int TABLE_NAME_CSV_INDEX = 0;
    private static final int TABLE_MINIMUM_CSV_INDEX = 1;
    private static final int TABLE_MAXIMUM_CSV_INDEX = 2;
    private static final int RESERVATION_START_TIME_CSV_INDEX = 0;
    private static final int RESERVATION_PARTY_SIZE_CSV_INDEX = 1;

    private static final int TABLE_DATA_PATH = 0;
    private static final int RESERVATION_DATA_PATH = 1;

    public static void main(String[] args) throws IOException {
        assertValidArguments(args);

        logger.info("Extract table information");
        String tableDataPath = args[TABLE_DATA_PATH];
        List<Table> tables = extractTableInfo(tableDataPath);

        logger.info("Extract reservation information");
        String reservationDataPath = args[RESERVATION_DATA_PATH];
        List<Reservation> reservations = extractReservationInfo(reservationDataPath);

        logger.info("Assigning reservations");
        List<ReservationAssignment> assignments = ReservationAssigner.run(tables, reservations);

        System.out.println("Reservation Assignments");
        int totalHeadCount = 0;
        int numberPeopleServed = 0;

        for (ReservationAssignment assignment : assignments) {
            System.out.println(assignment.toString());

            int partySize = assignment.getReservation().getPartySize();
            totalHeadCount += partySize;
            if (assignment.isAssigned()) {
                numberPeopleServed += partySize;
            }
        }

        System.out.println(
            String.format("Total # of people in reservation: %d, Served: %d", totalHeadCount, numberPeopleServed)
        );
    }

    /**
     * Simple argument validation
     *
     * @param args
     */
    private static void assertValidArguments(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <path to jar> <path to table data> <path to reservation data>");
            System.out.println("");
            System.out.println("Example");
            System.out.println("> java -jar ./dist/reservation-cli.jar ./data/table.csv ./data/reservation.csv");
            System.exit(1);
        }
    }

    /****************************** INPUT - START ******************************/
    /**
     * Reads from list of reservations, and convert to Reservation objects
     *
     * @param reservationInfoPath Path to reservation info
     * @return List of Reservation objects
     * @throws IOException
     */
    private static List<Reservation> extractReservationInfo(String reservationInfoPath) throws IOException {
        List<Reservation> reservations;
        logger.debug(String.format("Read reservation info from %s", reservationInfoPath));

        List<CSVRecord> records = CsvFileReader.parseAllLines(reservationInfoPath);

        int numRecords = records.size();
        logger.debug(String.format("Convert %d records into Reservation objects", numRecords));
        reservations = new ArrayList<Reservation>(numRecords);

        for (CSVRecord record : records) {
            int minimumHeadCount = Integer.parseInt(record.get(RESERVATION_START_TIME_CSV_INDEX));
            int maximumHeadCount = Integer.parseInt(record.get(RESERVATION_PARTY_SIZE_CSV_INDEX));

            reservations.add(new Reservation(minimumHeadCount, maximumHeadCount));
        }

        return reservations;
    }

    /**
     * Reads from table info, and convert input to Table objects
     *
     * @param tableInfoPath Path to table info
     * @return List of Table objects
     * @throws IOException
     */
    private static List<Table> extractTableInfo(String tableInfoPath) throws IOException {
        List<Table> tables;
        logger.debug(String.format("Read table info from %s", tableInfoPath));

        List<CSVRecord> records = CsvFileReader.parseAllLines(tableInfoPath);

        int numRecords = records.size();
        logger.debug(String.format("Convert %d records into Table objects", numRecords));
        tables = new ArrayList<Table>(numRecords);
        for (CSVRecord record : records) {
            String tableName = record.get(TABLE_NAME_CSV_INDEX);
            int minimumHeadCount = Integer.parseInt(record.get(TABLE_MINIMUM_CSV_INDEX));
            int maximumHeadCount = Integer.parseInt(record.get(TABLE_MAXIMUM_CSV_INDEX));

            tables.add(new Table(tableName, minimumHeadCount, maximumHeadCount));
        }

        return tables;
    }
    /****************************** INPUT - END ******************************/
}
