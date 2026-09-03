package cricketer.model;

public class Cricketer {

    private int playerId;
    private String name;
    private String country;
    private int age;
    private String role;

    private int matches;
    private int runs;
    private int highestScore;
    private int ballsFaced;
    private int dismissals;

    private int ballsBowled;
    private int runsConceded;
    private int wickets;

    public Cricketer() {}

    public Cricketer(int playerId, String name, int age,
                     String country, String role,
                     int matches, int runs, int highestScore,
                     int ballsFaced, int dismissals,
                     int ballsBowled, int runsConceded, int wickets) {
        this.playerId = playerId;
        this.name = name;
        this.age = age;
        this.country = country;
        this.role = role;
        this.matches = matches;
        this.runs = runs;
        this.highestScore = highestScore;
        this.ballsFaced = ballsFaced;
        this.dismissals = dismissals;
        this.ballsBowled = ballsBowled;
        this.runsConceded = runsConceded;
        this.wickets = wickets;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int v) { playerId = v; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public int getAge() { return age; }
    public void setAge(int v) { age = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { country = v; }
    public String getRole() { return role; }
    public void setRole(String v) { role = v; }

    public int getMatches() { return matches; }
    public void setMatches(int v) { matches = v; }
    public int getRuns() { return runs; }
    public void setRuns(int v) { runs = v; }
    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int v) { highestScore = v; }
    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int v) { ballsFaced = v; }
    public int getDismissals() { return dismissals; }
    public void setDismissals(int v) { dismissals = v; }

    public int getBallsBowled() { return ballsBowled; }
    public void setBallsBowled(int v) { ballsBowled = v; }
    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int v) { runsConceded = v; }
    public int getWickets() { return wickets; }
    public void setWickets(int v) { wickets = v; }

    // Derived values — calculated by the Java program.
    public double getBattingAverage() {
        return dismissals == 0 ? runs : (double) runs / dismissals;
    }

    public double getBattingStrikeRate() {
        return ballsFaced == 0 ? 0.0 : (double) runs * 100 / ballsFaced;
    }

    public double getBowlingAverage() {
        return wickets == 0 ? 0.0 : (double) runsConceded / wickets;
    }

    public double getBowlingStrikeRate() {
        return wickets == 0 ? 0.0 : (double) ballsBowled / wickets;
    }

    public double getBowlingEconomy() {
        return ballsBowled == 0 ? 0.0 : (double) runsConceded * 6 / ballsBowled;
    }

    @Override
    public String toString() {
        return String.format(
            "%-5d %-20s %-15s %-5d %-15s %6d %7d %7d %10.2f %10.2f %8d %10.2f %10.2f %10.2f",
            playerId, name, country, age, role,
            matches, runs, highestScore,
            getBattingAverage(), getBattingStrikeRate(),
            wickets, getBowlingAverage(),
            getBowlingStrikeRate(), getBowlingEconomy()
        );
    }
}
