package cricketer.dao;

import cricketer.model.Cricketer;
import cricketer.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CricketerDAO {

    /*
     * Main JOIN:
     * players + countries + roles + batting_stats + bowling_stats
     *
     * A LEFT JOIN is used for statistics so a newly-created player can
     * still be displayed before statistics are entered.
     */
    private static final String BASE_SELECT =
        "SELECT p.player_id, p.name, p.age, c.country_name, r.role_name, " +
        "COALESCE(b.matches,0) AS matches, COALESCE(b.runs,0) AS runs, " +
        "COALESCE(b.highest_score,0) AS highest_score, " +
        "COALESCE(b.balls_faced,0) AS balls_faced, " +
        "COALESCE(b.dismissals,0) AS dismissals, " +
        "COALESCE(w.balls_bowled,0) AS balls_bowled, " +
        "COALESCE(w.runs_conceded,0) AS runs_conceded, " +
        "COALESCE(w.wickets,0) AS wickets " +
        "FROM players p " +
        "INNER JOIN countries c ON p.country_id = c.country_id " +
        "INNER JOIN roles r ON p.role_id = r.role_id " +
        "LEFT JOIN batting_stats b ON p.player_id = b.player_id " +
        "LEFT JOIN bowling_stats w ON p.player_id = w.player_id ";

    private Cricketer map(ResultSet rs) throws SQLException {
        return new Cricketer(
            rs.getInt("player_id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("country_name"),
            rs.getString("role_name"),
            rs.getInt("matches"),
            rs.getInt("runs"),
            rs.getInt("highest_score"),
            rs.getInt("balls_faced"),
            rs.getInt("dismissals"),
            rs.getInt("balls_bowled"),
            rs.getInt("runs_conceded"),
            rs.getInt("wickets")
        );
    }

    public Cricketer findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.player_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Cricketer> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY p.player_id";
        return queryList(sql);
    }

    public List<Cricketer> findByName(String value) throws SQLException {
        String sql = BASE_SELECT +
            "WHERE LOWER(p.name) LIKE LOWER(?) ORDER BY p.name";
        return queryListWithString(sql, "%" + value + "%");
    }

    public List<Cricketer> findByCountry(String value) throws SQLException {
        String sql = BASE_SELECT +
            "WHERE LOWER(c.country_name) = LOWER(?) ORDER BY p.name";
        return queryListWithString(sql, value);
    }

    public List<Cricketer> findByRole(String value) throws SQLException {
        String sql = BASE_SELECT +
            "WHERE LOWER(r.role_name) = LOWER(?) ORDER BY p.name";
        return queryListWithString(sql, value);
    }

    public List<Cricketer> findTopByRuns(int limit) throws SQLException {
        String sql = BASE_SELECT +
            "ORDER BY COALESCE(b.runs,0) DESC LIMIT ?";
        return queryListWithLimit(sql, limit);
    }

    public List<Cricketer> findTopByWickets(int limit) throws SQLException {
        String sql = BASE_SELECT +
            "ORDER BY COALESCE(w.wickets,0) DESC LIMIT ?";
        return queryListWithLimit(sql, limit);
    }

    public List<Cricketer> sortByRuns() throws SQLException {
        String sql = BASE_SELECT +
            "ORDER BY COALESCE(b.runs,0) DESC, p.name";
        return queryList(sql);
    }

    public List<Cricketer> sortByAge() throws SQLException {
        String sql = BASE_SELECT +
            "ORDER BY p.age ASC, p.name";
        return queryList(sql);
    }

    /*
     * Subquery:
     * Find player(s) whose runs equal the maximum runs in batting_stats.
     */
    public List<Cricketer> getHighestRunScorers() throws SQLException {
        String sql = BASE_SELECT +
            "WHERE COALESCE(b.runs,0) = " +
            "(SELECT MAX(runs) FROM batting_stats)";
        return queryList(sql);
    }

    /*
     * Subquery:
     * Find player(s) whose wickets equal the maximum wickets.
     */
    public List<Cricketer> getHighestWicketTakers() throws SQLException {
        String sql = BASE_SELECT +
            "WHERE COALESCE(w.wickets,0) = " +
            "(SELECT MAX(wickets) FROM bowling_stats)";
        return queryList(sql);
    }

    /*
     * Correlated subquery:
     * Select players who have scored above the average runs of all players.
     */
    public List<Cricketer> getAboveAverageRunScorers() throws SQLException {
        String sql = BASE_SELECT +
            "WHERE COALESCE(b.runs,0) > " +
            "(SELECT AVG(b2.runs) FROM batting_stats b2) " +
            "ORDER BY b.runs DESC";
        return queryList(sql);
    }

    /*
     * EXISTS subquery:
     * Players who have participated in at least one match.
     */
    public List<Cricketer> getPlayersWithMatchParticipation() throws SQLException {
        String sql = BASE_SELECT +
            "WHERE EXISTS (" +
            "SELECT 1 FROM player_match_stats pms " +
            "WHERE pms.player_id = p.player_id) " +
            "ORDER BY p.name";
        return queryList(sql);
    }

    /*
     * GROUP BY + JOIN:
     * Aggregate player performance across matches.
     */
    public List<String> getMatchPerformanceSummary() throws SQLException {
        String sql =
            "SELECT p.name, COUNT(DISTINCT m.match_id) AS matches_played, " +
            "SUM(pms.runs) AS match_runs, SUM(pms.wickets) AS match_wickets " +
            "FROM player_match_stats pms " +
            "INNER JOIN players p ON p.player_id = pms.player_id " +
            "INNER JOIN matches m ON m.match_id = pms.match_id " +
            "GROUP BY p.player_id, p.name " +
            "ORDER BY match_runs DESC";

        List<String> result = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(String.format(
                    "%-20s Matches: %-3d Runs: %-5d Wickets: %-3d",
                    rs.getString("name"),
                    rs.getInt("matches_played"),
                    rs.getInt("match_runs"),
                    rs.getInt("match_wickets")
                ));
            }
        }
        return result;
    }

    /*
     * Transaction:
     * A new player requires rows in players, batting_stats and bowling_stats.
     * Either all three succeed or none is committed.
     */
    public void insert(Cricketer p, int countryId, int roleId)
            throws SQLException {

        String playerSql =
            "INSERT INTO players(player_id,name,age,country_id,role_id) " +
            "VALUES(?,?,?,?,?)";

        String battingSql =
            "INSERT INTO batting_stats " +
            "(player_id,matches,runs,highest_score,balls_faced,dismissals) " +
            "VALUES(?,?,?,?,?,?)";

        String bowlingSql =
            "INSERT INTO bowling_stats " +
            "(player_id,balls_bowled,runs_conceded,wickets) " +
            "VALUES(?,?,?,?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement player =
                     con.prepareStatement(playerSql);
                 PreparedStatement batting =
                     con.prepareStatement(battingSql);
                 PreparedStatement bowling =
                     con.prepareStatement(bowlingSql)) {

                player.setInt(1, p.getPlayerId());
                player.setString(2, p.getName());
                player.setInt(3, p.getAge());
                player.setInt(4, countryId);
                player.setInt(5, roleId);
                player.executeUpdate();

                batting.setInt(1, p.getPlayerId());
                batting.setInt(2, p.getMatches());
                batting.setInt(3, p.getRuns());
                batting.setInt(4, p.getHighestScore());
                batting.setInt(5, p.getBallsFaced());
                batting.setInt(6, p.getDismissals());
                batting.executeUpdate();

                bowling.setInt(1, p.getPlayerId());
                bowling.setInt(2, p.getBallsBowled());
                bowling.setInt(3, p.getRunsConceded());
                bowling.setInt(4, p.getWickets());
                bowling.executeUpdate();

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void update(Cricketer p, int countryId, int roleId)
            throws SQLException {

        String playerSql =
            "UPDATE players SET name=?, age=?, country_id=?, role_id=? " +
            "WHERE player_id=?";

        String battingSql =
            "UPDATE batting_stats SET matches=?, runs=?, highest_score=?, " +
            "balls_faced=?, dismissals=? WHERE player_id=?";

        String bowlingSql =
            "UPDATE bowling_stats SET balls_bowled=?, runs_conceded=?, " +
            "wickets=? WHERE player_id=?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(playerSql);
                 PreparedStatement p2 = con.prepareStatement(battingSql);
                 PreparedStatement p3 = con.prepareStatement(bowlingSql)) {

                p1.setString(1, p.getName());
                p1.setInt(2, p.getAge());
                p1.setInt(3, countryId);
                p1.setInt(4, roleId);
                p1.setInt(5, p.getPlayerId());
                p1.executeUpdate();

                p2.setInt(1, p.getMatches());
                p2.setInt(2, p.getRuns());
                p2.setInt(3, p.getHighestScore());
                p2.setInt(4, p.getBallsFaced());
                p2.setInt(5, p.getDismissals());
                p2.setInt(6, p.getPlayerId());
                p2.executeUpdate();

                p3.setInt(1, p.getBallsBowled());
                p3.setInt(2, p.getRunsConceded());
                p3.setInt(3, p.getWickets());
                p3.setInt(4, p.getPlayerId());
                p3.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM players WHERE player_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getCountryId(String name) throws SQLException {
        String sql = "SELECT country_id FROM countries " +
                     "WHERE LOWER(country_name)=LOWER(?)";
        return getId(sql, name);
    }

    public int getRoleId(String name) throws SQLException {
        String sql = "SELECT role_id FROM roles " +
                     "WHERE LOWER(role_name)=LOWER(?)";
        return getId(sql, name);
    }

    private int getId(String sql, String value) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /*
     * Demonstrates Statement and ResultSetMetaData.
     * It is used only for a fixed SQL statement, not user input.
     */
    public void showTableColumns() throws SQLException {
        String sql = "SELECT * FROM players LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();

            System.out.println("\nPLAYERS TABLE COLUMNS");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println(
                    i + ". " + meta.getColumnName(i) +
                    " - " + meta.getColumnTypeName(i)
                );
            }
        }
    }

    /*
     * Demonstrates DatabaseMetaData.
     */
    public void showDatabaseInfo() throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();

            System.out.println("\nDATABASE INFORMATION");
            System.out.println("Database : " + meta.getDatabaseProductName());
            System.out.println("Version  : " + meta.getDatabaseProductVersion());
            System.out.println("Driver   : " + meta.getDriverName());
            System.out.println("JDBC     : " + meta.getJDBCMajorVersion() +
                               "." + meta.getJDBCMinorVersion());
        }
    }

    private List<Cricketer> queryList(String sql) throws SQLException {
        List<Cricketer> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private List<Cricketer> queryListWithString(
            String sql, String value) throws SQLException {

        List<Cricketer> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private List<Cricketer> queryListWithLimit(
            String sql, int limit) throws SQLException {

        List<Cricketer> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }
}
