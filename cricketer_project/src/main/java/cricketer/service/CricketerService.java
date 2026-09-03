package cricketer.service;

import cricketer.dao.CricketerDAO;
import cricketer.exception.*;
import cricketer.model.Cricketer;

import java.sql.SQLException;
import java.util.List;

public class CricketerService {

    private final CricketerDAO dao = new CricketerDAO();

    public void addPlayer(Cricketer p)
            throws SQLException, InvalidPlayerDataException,
                   DuplicatePlayerException {

        validate(p);

        if (dao.findById(p.getPlayerId()) != null) {
            throw new DuplicatePlayerException(
                "ERROR: Player ID " + p.getPlayerId() + " already exists.");
        }

        int countryId = dao.getCountryId(p.getCountry());
        if (countryId == -1) {
            throw new InvalidPlayerDataException(
                "ERROR: Country does not exist in countries table.");
        }

        int roleId = dao.getRoleId(p.getRole());
        if (roleId == -1) {
            throw new InvalidPlayerDataException(
                "ERROR: Role does not exist. Use Batsman, Bowler or All Rounder.");
        }

        dao.insert(p, countryId, roleId);
    }

    public void updatePlayer(Cricketer p)
            throws SQLException, InvalidPlayerDataException,
                   PlayerNotFoundException {

        validate(p);
        findById(p.getPlayerId());

        int countryId = dao.getCountryId(p.getCountry());
        if (countryId == -1) {
            throw new InvalidPlayerDataException(
                "ERROR: Country does not exist in countries table.");
        }

        int roleId = dao.getRoleId(p.getRole());
        if (roleId == -1) {
            throw new InvalidPlayerDataException(
                "ERROR: Role does not exist.");
        }

        dao.update(p, countryId, roleId);
    }

    public void deletePlayer(int id)
            throws SQLException, PlayerNotFoundException {
        findById(id);
        dao.delete(id);
    }

    public Cricketer findById(int id)
            throws SQLException, PlayerNotFoundException {
        Cricketer p = dao.findById(id);
        if (p == null) {
            throw new PlayerNotFoundException(
                "ERROR: Player ID " + id + " not found.");
        }
        return p;
    }

    public List<Cricketer> getAllPlayers() throws SQLException {
        return dao.findAll();
    }

    public List<Cricketer> searchByName(String s) throws SQLException {
        return dao.findByName(s);
    }

    public List<Cricketer> searchByCountry(String s) throws SQLException {
        return dao.findByCountry(s);
    }

    public List<Cricketer> searchByRole(String s) throws SQLException {
        return dao.findByRole(s);
    }

    public List<Cricketer> topRuns() throws SQLException {
        return dao.findTopByRuns(5);
    }

    public List<Cricketer> topWickets() throws SQLException {
        return dao.findTopByWickets(5);
    }

    public List<Cricketer> sortByRuns() throws SQLException {
        return dao.sortByRuns();
    }

    public List<Cricketer> sortByAge() throws SQLException {
        return dao.sortByAge();
    }

    public List<Cricketer> highestRuns() throws SQLException {
        return dao.getHighestRunScorers();
    }

    public List<Cricketer> highestWickets() throws SQLException {
        return dao.getHighestWicketTakers();
    }

    public List<Cricketer> aboveAverageRuns() throws SQLException {
        return dao.getAboveAverageRunScorers();
    }

    public List<Cricketer> matchParticipants() throws SQLException {
        return dao.getPlayersWithMatchParticipation();
    }

    public List<String> matchSummary() throws SQLException {
        return dao.getMatchPerformanceSummary();
    }

    public void showTableColumns() throws SQLException {
        dao.showTableColumns();
    }

    public void showDatabaseInfo() throws SQLException {
        dao.showDatabaseInfo();
    }

    private void validate(Cricketer p)
            throws InvalidPlayerDataException {

        if (p == null) {
            throw new InvalidPlayerDataException("ERROR: Player data cannot be null.");
        }
        if (p.getPlayerId() <= 0) {
            throw new InvalidPlayerDataException(
                "ERROR: Player ID must be greater than 0.");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new InvalidPlayerDataException(
                "ERROR: Player name cannot be empty.");
        }
        if (p.getCountry() == null || p.getCountry().trim().isEmpty()) {
            throw new InvalidPlayerDataException(
                "ERROR: Country cannot be empty.");
        }
        if (p.getRole() == null || p.getRole().trim().isEmpty()) {
            throw new InvalidPlayerDataException(
                "ERROR: Role cannot be empty.");
        }
        if (p.getAge() <= 0 || p.getAge() > 100) {
            throw new InvalidPlayerDataException("ERROR: Invalid player age.");
        }
        if (p.getMatches() < 0 || p.getRuns() < 0 ||
            p.getHighestScore() < 0 || p.getBallsFaced() < 0 ||
            p.getDismissals() < 0 || p.getBallsBowled() < 0 ||
            p.getRunsConceded() < 0 || p.getWickets() < 0) {
            throw new InvalidPlayerDataException(
                "ERROR: Statistics cannot be negative.");
        }
    }
}
