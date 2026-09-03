package cricketer.main;

import cricketer.exception.*;
import cricketer.model.Cricketer;
import cricketer.service.CricketerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CricketerManagementApp {

    static Scanner scanner = new Scanner(System.in);
    static CricketerService service = new CricketerService();

    public static void main(String[] args) {

        try {
            service.getAllPlayers();
        } catch (SQLException e) {
            System.out.println("ERROR: Database connection failed.");
            System.out.println("Check MySQL, database name, username and password.");
            System.out.println("Details: " + e.getMessage());
            return;
        }

        int choice;

        do {
            menu();
            choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1: add(); break;
                    case 2: displayPlayers(service.getAllPlayers()); break;
                    case 3: displayPlayers(service.searchByName(readString("Enter player name: "))); break;
                    case 4: displayPlayers(service.searchByCountry(readString("Enter country: "))); break;
                    case 5: displayPlayers(service.searchByRole(readString("Enter role: "))); break;
                    case 6: update(); break;
                    case 7: delete(); break;
                    case 8: displayDetailedList(service.highestRuns(), "HIGHEST RUN SCORER"); break;
                    case 9: displayDetailedList(service.highestWickets(), "HIGHEST WICKET TAKER"); break;
                    case 10: highestIndividualScore(); break;
                    case 11: displayPlayers(service.topRuns()); break;
                    case 12: displayPlayers(service.topWickets()); break;
                    case 13: statistics(); break;
                    case 14: displayPlayers(service.sortByRuns()); break;
                    case 15: displayPlayers(service.sortByAge()); break;
                    case 16: System.out.println("Data is automatically stored in MySQL."); break;
                    case 17: displayPlayers(service.aboveAverageRuns()); break;
                    case 18: displayPlayers(service.matchParticipants()); break;
                    case 19: displayMatchSummary(); break;
                    case 20: service.showTableColumns(); break;
                    case 21: service.showDatabaseInfo(); break;
                    case 0: System.out.println("Exiting application."); break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("DATABASE ERROR: " + e.getMessage());
            } catch (PlayerNotFoundException |
                     DuplicatePlayerException |
                     InvalidPlayerDataException e) {
                System.out.println(e.getMessage());
            }

        } while (choice != 0);

        scanner.close();
    }

    static void menu() {
        System.out.println("\n==============================================");
        System.out.println("       CRICKETER MANAGEMENT SYSTEM");
        System.out.println("==============================================");
        System.out.println("1. Add Cricketer");
        System.out.println("2. View All Cricketers");
        System.out.println("3. Search Cricketer by Name");
        System.out.println("4. Search by Country");
        System.out.println("5. Search by Role");
        System.out.println("6. Update Cricketer");
        System.out.println("7. Delete Cricketer");
        System.out.println("8. Highest Run Scorer");
        System.out.println("9. Highest Wicket Taker");
        System.out.println("10. Highest Individual Score");
        System.out.println("11. Top 5 Run Scorers");
        System.out.println("12. Top 5 Wicket Takers");
        System.out.println("13. Display Player Statistics");
        System.out.println("14. Sort Players by Runs");
        System.out.println("15. Sort Players by Age");
        System.out.println("16. Save Data");
        System.out.println("17. Players Above Average Runs (Subquery)");
        System.out.println("18. Players With Match Participation (EXISTS)");
        System.out.println("19. Match Performance Summary (JOIN + GROUP BY)");
        System.out.println("20. Show Table Columns (ResultSetMetaData)");
        System.out.println("21. Show Database/Driver Info (DatabaseMetaData)");
        System.out.println("0. Exit");
        System.out.println("==============================================");
    }

    static void add()
            throws SQLException, InvalidPlayerDataException,
                   DuplicatePlayerException {

        System.out.println("\nADD CRICKETER");
        Cricketer p = readPlayer(readInt("Player ID: "));
        service.addPlayer(p);
        System.out.println("Player added successfully.");
    }

    static void update()
            throws SQLException, InvalidPlayerDataException,
                   PlayerNotFoundException {

        int id = readInt("Enter Player ID to update: ");
        service.findById(id);
        Cricketer p = readPlayer(id);
        service.updatePlayer(p);
        System.out.println("Player updated successfully.");
    }

    static Cricketer readPlayer(int id) {
        String name = readString("Name: ");
        int age = readInt("Age: ");
        String country = readString("Country: ");
        String role = readString("Role: ");

        System.out.println("\nRAW BATTING DATA");
        int matches = readInt("Matches: ");
        int runs = readInt("Runs: ");
        int highestScore = readInt("Highest Score: ");
        int ballsFaced = readInt("Balls Faced: ");
        int dismissals = readInt("Dismissals: ");

        System.out.println("\nRAW BOWLING DATA");
        int ballsBowled = readInt("Balls Bowled: ");
        int runsConceded = readInt("Runs Conceded: ");
        int wickets = readInt("Wickets: ");

        return new Cricketer(
            id, name, age, country, role,
            matches, runs, highestScore,
            ballsFaced, dismissals,
            ballsBowled, runsConceded, wickets
        );
    }

    static void delete()
            throws SQLException, PlayerNotFoundException {
        int id = readInt("Enter Player ID to delete: ");
        service.deletePlayer(id);
        System.out.println("Player deleted successfully.");
    }

    static void statistics()
            throws SQLException, PlayerNotFoundException {
        int id = readInt("Enter Player ID: ");
        Cricketer p = service.findById(id);
        detailed(p);
    }

    static void highestIndividualScore() throws SQLException {
        List<Cricketer> list = service.getAllPlayers();
        if (list.isEmpty()) {
            System.out.println("No players found.");
            return;
        }

        Cricketer best = list.get(0);
        for (Cricketer p : list) {
            if (p.getHighestScore() > best.getHighestScore()) {
                best = p;
            }
        }

        displayDetailedList(
            java.util.Collections.singletonList(best),
            "HIGHEST INDIVIDUAL SCORE"
        );
    }

    static void displayDetailedList(
            List<Cricketer> list, String title) {
        System.out.println("\n" + title);
        if (list.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        detailed(list.get(0));
    }

    static void detailed(Cricketer p) {
        System.out.println("------------------------------------------");
        System.out.println("Player ID           : " + p.getPlayerId());
        System.out.println("Name                : " + p.getName());
        System.out.println("Country             : " + p.getCountry());
        System.out.println("Age                 : " + p.getAge());
        System.out.println("Role                : " + p.getRole());
        System.out.println("Matches             : " + p.getMatches());
        System.out.println("Runs                : " + p.getRuns());
        System.out.println("Highest Score       : " + p.getHighestScore());
        System.out.printf ("Batting Average     : %.2f%n", p.getBattingAverage());
        System.out.printf ("Batting Strike Rate : %.2f%n", p.getBattingStrikeRate());
        System.out.println("Wickets             : " + p.getWickets());
        System.out.println("Runs Conceded       : " + p.getRunsConceded());
        System.out.printf ("Bowling Average     : %.2f%n", p.getBowlingAverage());
        System.out.printf ("Bowling Strike Rate : %.2f%n", p.getBowlingStrikeRate());
        System.out.printf ("Bowling Economy     : %.2f%n", p.getBowlingEconomy());
        System.out.println("------------------------------------------");
    }

    static void displayPlayers(List<Cricketer> list) {
        if (list.isEmpty()) {
            System.out.println("No players found.");
            return;
        }

        System.out.printf(
            "%-5s %-20s %-15s %-5s %-15s %7s %7s %7s %10s %10s %8s %10s %10s %10s%n",
            "ID","Name","Country","Age","Role","Matches","Runs","Highest",
            "Bat Avg","Bat SR","Wickets","Bowl Avg","Bowl SR","Economy");

        System.out.println(
            "-------------------------------------------------------------------------------------------------------------------------------------");

        for (Cricketer p : list) {
            System.out.println(p);
        }
    }

    static void displayMatchSummary() throws SQLException {
        System.out.println("\nMATCH PERFORMANCE SUMMARY");
        for (String row : service.matchSummary()) {
            System.out.println(row);
        }
    }

    static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
