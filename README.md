To Run:
1. Download the JAR from dist/reservation-cli.jar
2. Run `java -jar <path to jar> <path to table csv> <path to reservation csv>`

Code Entry Point:
- ReservationRunner.main(...) (See ReservationRunner.java)

Overview
The algorithm uses a greedy approach in maximizing the number of people a restaurant can serve per shift.

At a high-level, the code does the following:
1. Sort tables by table size, with tables of highest capacity going first.
2. Sort reservations by party size, with bigger parties going first.
3. For each table, starting from the table with highest capacity, find the largest party it is able to serve among the
   list of available reservation. Repeat until the table can no longer handle any more open reservations.

Assumptions
1. Data is well-formatted.
- Time must be between 1700 to 2100.
- See input in data/ as example.

To Package:
`mvn package`

To Update JAR:
`mvn package`
`cp ./target/reservation-cli-1.0-SNAPSHOT-jar-with-dependencies.jar ./dist/reservation-cli.jar`
