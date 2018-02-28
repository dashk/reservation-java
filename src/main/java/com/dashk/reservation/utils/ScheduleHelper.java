package com.dashk.reservation.utils;

public class ScheduleHelper {
    /**
     * Creates an empty schedule
     * Schedule is a boolean array, with each cell representing a time slot of 15 minutes, and it starts with
     * the beginning of the restaurant starts time.
     *
     * Example
     * Start Time: 1700, End Time: 2145
     *
     *
     * @return
     */
    public static boolean[] createEmptySchedule() {
        return new boolean[Constants.TIME_SLOT_LENGTH];
    }

    /**
     * Computes index of given timeslot in a schedule
     *
     * @param time
     * @return
     */
    public static int getScheduleIndexByTime(int time) {
        int adjustedTime = time - Constants.START_TIME;
        int hourTimeSlot = (adjustedTime / 100) * Constants.TIME_SLOT_PER_HOUR;

        // Extract the minute portion of adjusted time
        int minute = adjustedTime % 100;

        // Compute index
        return hourTimeSlot + getMinuteTimeslotAdjustment(minute);
    }

    /**
     * Returns true if restaurant is open given the time
     *
     * @param time
     * @return
     */
    public static boolean isSeatingAvailable(int time) {
        return time <= Constants.LAST_SEATING_TIME;
    }

    /**
     * Returns timeslot given party size occupies in a schedule
     *
     * @param partySize
     * @return
     */
    public static int getOccupiedTimeslotByPartySize(int partySize) {
        if (partySize == 1) {
            return 3;
        } else if (partySize >= 2 && partySize <= 3) {
            return 6;
        } else if (partySize >= 4 && partySize <= 6) {
            return 8;
        } else if (partySize >= 7 && partySize <= 10){
            return 10;
        } else {
            throw new IllegalArgumentException(String.format("Unsupported party size %d", partySize));
        }
    }

    private static int getMinuteTimeslotAdjustment(int minute) {
        switch (minute) {
            case 0: return 0;
            case 15: return 1;
            case 30: return 2;
            case 45: return 3;
            default:
                throw new IllegalArgumentException(String.format("Unrecognized minute %d", minute));
        }
    }
}
