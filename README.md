To Run:
1. Download the JAR from dist/reservation.jar
2. Run java -jar <path to jar> <path to CSV file> "<time to search>"

Code Entry Point:
- AvailabilityRunner.findAvailableSalesRep(...) (See AvailabilityRunner.java)

Assumptions
1. CSV is expected to be well formatted. (RFC4180)

2. Time is expected to be well formatted.
   Format: "<Time Range> [, <Time Range>]" (At least one time range must be specified)
   Time Range = "<Day of week>[-<Day of week>] <Time> - <Time>"
   Day of week = Mon, Tue, Wed, Thu, Fri
   Time = "<Hour>:<Minute> <Time of day>"
   Hour = 1, ..., 12
      (Note: 0 hour is considered to be invalid. It should be specified as 12:00 a or 12:30 a).
   Minute = 00, 30
   Time of day = am, pm

3. Each time range is expected to cover a slot with at least 30 minutes.

4. Mon-Wed 5:00 am - 7:00 am means, on Monday, Tuesday & Wednesday, Sales Rep is available from 5am to 7am, NOT
   Sales Rep is available from Monday at 5am till Wednesday at 7pm.

5. Both input search time & scheduling time are expected to be incremented every 30 minutes, including the input time.
   Any minutes other than 00 & 30 are considered invalid.

6. Sales Rep (Email) is expected to be unique per CSV file.

7. Time range will not cross the day boundary.
   e.g. If we want to find a time from Tuesday 10pm to Wednesday 2am, it will be specified as...
        Tue 10:00 pm - 12:00 am, Wed 12:00 am - 2:00 am

8. To mark a Sales Rep as available all day, you will need to specify the time as 12:00 am to 12:00 am.
   e.g. Mon-Wed 12:00 am - 12:00 am <-- This indicates the Sales Rep is available all day from Monday through Wednesday

To Package:
mvn package
