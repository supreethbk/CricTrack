CREATE DATABASE IF NOT EXISTS cricketer_db;
USE cricketer_db;

DROP TABLE IF EXISTS player_match_stats;
DROP TABLE IF EXISTS bowling_stats;
DROP TABLE IF EXISTS batting_stats;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS countries;

CREATE TABLE countries (
    country_id INT AUTO_INCREMENT PRIMARY KEY,
    country_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE players (
    player_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    country_id INT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT fk_player_country
        FOREIGN KEY (country_id) REFERENCES countries(country_id),
    CONSTRAINT fk_player_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id),
    CONSTRAINT chk_player_age CHECK (age BETWEEN 1 AND 100)
);

CREATE TABLE batting_stats (
    player_id INT PRIMARY KEY,
    matches INT NOT NULL DEFAULT 0,
    runs INT NOT NULL DEFAULT 0,
    highest_score INT NOT NULL DEFAULT 0,
    balls_faced INT NOT NULL DEFAULT 0,
    dismissals INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_batting_player
        FOREIGN KEY (player_id) REFERENCES players(player_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_batting_matches CHECK (matches >= 0),
    CONSTRAINT chk_batting_runs CHECK (runs >= 0),
    CONSTRAINT chk_batting_highest CHECK (highest_score >= 0),
    CONSTRAINT chk_batting_balls CHECK (balls_faced >= 0),
    CONSTRAINT chk_batting_dismissals CHECK (dismissals >= 0)
);

CREATE TABLE bowling_stats (
    player_id INT PRIMARY KEY,
    balls_bowled INT NOT NULL DEFAULT 0,
    runs_conceded INT NOT NULL DEFAULT 0,
    wickets INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_bowling_player
        FOREIGN KEY (player_id) REFERENCES players(player_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_bowling_balls CHECK (balls_bowled >= 0),
    CONSTRAINT chk_bowling_runs CHECK (runs_conceded >= 0),
    CONSTRAINT chk_bowling_wickets CHECK (wickets >= 0)
);

CREATE TABLE matches (
    match_id INT PRIMARY KEY,
    match_date DATE NOT NULL,
    opponent VARCHAR(100) NOT NULL,
    venue VARCHAR(150) NOT NULL,
    result VARCHAR(20) NOT NULL
);

CREATE TABLE player_match_stats (
    match_id INT NOT NULL,
    player_id INT NOT NULL,
    runs INT NOT NULL DEFAULT 0,
    balls_faced INT NOT NULL DEFAULT 0,
    wickets INT NOT NULL DEFAULT 0,
    balls_bowled INT NOT NULL DEFAULT 0,
    runs_conceded INT NOT NULL DEFAULT 0,
    PRIMARY KEY (match_id, player_id),
    CONSTRAINT fk_pms_match
        FOREIGN KEY (match_id) REFERENCES matches(match_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pms_player
        FOREIGN KEY (player_id) REFERENCES players(player_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_pms_runs CHECK (runs >= 0),
    CONSTRAINT chk_pms_balls_faced CHECK (balls_faced >= 0),
    CONSTRAINT chk_pms_wickets CHECK (wickets >= 0),
    CONSTRAINT chk_pms_balls_bowled CHECK (balls_bowled >= 0),
    CONSTRAINT chk_pms_runs_conceded CHECK (runs_conceded >= 0)
);

INSERT INTO countries(country_name) VALUES
('India'), ('England'), ('Australia'), ('New Zealand'), ('Pakistan');

INSERT INTO roles(role_name) VALUES
('Batsman'), ('Bowler'), ('All Rounder');

INSERT INTO players VALUES
(101, 'Virat Kohli', 37, 1, 1),
(102, 'Rohit Sharma', 39, 1, 1),
(103, 'Jasprit Bumrah', 32, 1, 2),
(104, 'Joe Root', 35, 2, 1),
(105, 'Steve Smith', 37, 3, 1),
(106, 'Kane Williamson', 36, 4, 1),
(107, 'Ravindra Jadeja', 37, 1, 3),
(108, 'Ben Stokes', 35, 2, 3),
(109, 'Babar Azam', 31, 5, 1),
(110, 'Pat Cummins', 33, 3, 2);

-- Raw batting data. Averages and strike rates are deliberately NOT stored.
INSERT INTO batting_stats VALUES
(101, 292, 13848, 254, 23600, 236),
(102, 499, 19400, 264, 39640, 396),
(103, 196, 1000, 43, 6667, 66),
(104, 350, 13000, 262, 25896, 259),
(105, 330, 10500, 239, 21212, 212),
(106, 320, 9500, 251, 19487, 195),
(107, 300, 6500, 175, 18310, 183),
(108, 260, 7000, 258, 18182, 182),
(109, 280, 12000, 196, 22901, 229),
(110, 250, 2500, 95, 12195, 122);

-- Raw bowling data. Bowling average, strike rate and economy are derived by Java.
INSERT INTO bowling_stats VALUES
(101, 300, 225, 5),
(102, 2820, 1692, 47),
(103, 10800, 4860, 450),
(104, 2400, 2000, 20),
(105, 3300, 2800, 28),
(106, 4440, 3108, 37),
(107, 13200, 6380, 550),
(108, 15600, 9600, 300),
(109, 720, 660, 12),
(110, 30000, 12700, 500);

INSERT INTO matches VALUES
(1, '2026-01-10', 'Australia', 'Melbourne', 'Won'),
(2, '2026-02-15', 'England', 'Mumbai', 'Won'),
(3, '2026-03-20', 'New Zealand', 'Auckland', 'Lost'),
(4, '2026-04-11', 'Pakistan', 'Dubai', 'Won'),
(5, '2026-05-08', 'Australia', 'Sydney', 'Lost');

INSERT INTO player_match_stats VALUES
(1,101,85,92,0,0,0),
(1,102,112,105,0,0,0),
(1,103,12,15,4,60,35),
(1,107,45,52,2,60,40),
(1,110,10,18,3,60,42),
(2,101,74,80,0,0,0),
(2,102,126,118,0,0,0),
(2,104,98,104,0,0,0),
(2,107,36,40,3,60,38),
(2,108,55,48,2,60,45),
(3,106,91,99,0,0,0),
(3,103,8,11,5,60,28),
(3,110,18,21,2,60,50),
(4,109,105,110,0,0,0),
(4,107,62,57,4,60,35),
(4,103,5,9,3,60,30),
(5,105,88,94,0,0,0),
(5,108,73,68,1,60,51),
(5,110,22,25,4,60,44);
