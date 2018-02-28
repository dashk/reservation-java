package com.dashk.reservation.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class ScheduleHelperTest extends TestCase {

    @Test
    public void testGetScheduleIndexByTimeShouldReturnZeroWhenTimeIsTheStartOfSchedule() {
        int time = Constants.START_TIME;
        assertEquals(0, ScheduleHelper.getScheduleIndexByTime(time));
    }

    @Test
    public void testGetScheduleIndexByTimeShouldReturnMaxTimeslotWhenTimeIsTheEndOfSchedule() {
        int time = 2145;
        assertEquals(Constants.TIME_SLOT_LENGTH - 1, ScheduleHelper.getScheduleIndexByTime(time));
    }
}
