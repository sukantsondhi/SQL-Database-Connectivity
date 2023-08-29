# SQL-Database-Connectivity

1. The program should output some statistical information about the data it reads from the file, as described in what follows
(in the same order they are listed).

2. When you are required to list a collection of records/attributes, you are meant to output them to the standard output,
where each record is written in a new line.

4. After the program lists each of the clauses above, it should output section separators lines like “########### ith Query
########” to make the output easy to read.

6. If you are requested to list 5 elements/records and there are less than 5 such elements/records, the program should
simply print as many as there are.


Query 1
List the 5 distinct Unique Carriers (UniqueCarrier) that suffer the largest number of flight delays (either on arrival or on
departure) and the number of delays they had in descending order (with respect to the number of delays).
Example of output:
################## 1st Query ###############
WN 5665
AA 809
B6 396
EV 330
UA 252


Query 2
List the top 5 distinct cities in which departure delays occur the most (irrespective of the length of the delay) in descending
order (with respect to number of delays).
Example of output:
################## 2nd Query ###############
Chicago 752
Las Vegas 695
Dallas 425
Los Angeles 375
Phoenix 358


Query 3
List the 2nd to 6th distinct destinations (Dest) that has the highest total amount of minutes in arrival delays (ArrDelay) in
descending order (with respect to total arrival delay minutes), together with their total arrival delay minutes. Namely, if we
denote the six distinct
destinations with the highest total arrival delay minutes by a1, a2, a3, a4, a5, a6, then the program should output a2, a3 ,a4
,a5 ,a6 (together with the total arrival delay minutes for each destination, as shown in the example below).
Example of output:
################## 3rd Query ###############
ORD 16073
OAK 13655
LAX 13617
PHX 12817
SAN 11121


Query 4
For every state that has at least 10 airports, list the state and the number of the airports.


Query 5
List the top 5 distinct states in which a flight between different airports within the same state has been delayed (either in
arrival or departure) by descending order with respect to number of such delays (irrespective of the length of the delay),
together with the number of such delays (you can assume that no flight has both origin and destination the same airport, and
that every occurrence of a line in the delayedFlight file records a delay in either departure or arrival).
