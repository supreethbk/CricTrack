CricTrack is a Cricketer Management System

This is ONE Java console application combining the original assignment requirements with the JDBC/MySQL database implementation.
What is included
Java concepts

    Classes and Objects
    Encapsulation
    Constructors
    Getters/Setters
    ArrayList/List
    Collections
    Comparator and Lambda expressions
    Java 8 Streams
    Exception Handling
    Custom Exceptions
    Menu-driven programming
    CRUD operations
    Validation
    File-independent database persistence

Database

The application uses MySQL through JDBC and contains 7 related tables:

    countries
    roles
    players
    batting_stats
    bowling_stats
    matches
    player_match_stats

The schema demonstrates:

    Primary keys
    Foreign keys
    Composite primary key
    UNIQUE constraints
    NOT NULL constraints
    CHECK constraints
    DEFAULT values
    ON DELETE CASCADE

JDBC / java.sql concepts

The project demonstrates:

    Connection
    DriverManager
    PreparedStatement
    Statement
    ResultSet
    SQLException
    ResultSetMetaData
    DatabaseMetaData
    Transactions

SQL concepts

The project demonstrates:

    INNER JOIN
    LEFT/related joins where applicable
    GROUP BY
    HAVING
    Aggregate functions
    Subqueries
    EXISTS
    Transaction handling

Derived cricket statistics

Derived values are calculated from stored raw statistics instead of being unnecessarily stored:

    Batting Average
    Batting Strike Rate
    Bowling Average
    Bowling Strike Rate
    Bowling Economy Rate

Setup

    Install MySQL and create the database using: database/cricketer_db.sql

    Open src/main/java/cricketer/util/DBConnection.java.

    Change:
        MySQL username
        MySQL password

    Build/run with Maven: mvn clean compile mvn exec:java

The exact Maven command may also be run from an IDE after importing the project.
Important

This final version is intended as the integrated database version of the assignment. The database is the persistence layer; the original Java assignment features are implemented on top of it.
