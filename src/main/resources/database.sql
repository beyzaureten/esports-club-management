-- E-Sports Club Management System
-- Database Script

CREATE DATABASE IF NOT EXISTS esports_db;
USE esports_db;

-- 1. USERS tablosu
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(10) NOT NULL DEFAULT 'MEMBER', -- 'ADMIN' veya 'MEMBER'
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE' -- 'ACTIVE' veya 'INACTIVE'
);

-- 2. GAMES tablosu
CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    mode VARCHAR(20) NOT NULL
);

-- 3. TEAMS tablosu
CREATE TABLE IF NOT EXISTS teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    game_id INT NOT NULL,
    max_capacity INT NOT NULL DEFAULT 5,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (game_id) REFERENCES games(id)
);

-- 4. TOURNAMENTS tablosu
CREATE TABLE IF NOT EXISTS tournaments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    game_id INT NOT NULL,
    max_teams INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'UPCOMING',
    FOREIGN KEY (game_id) REFERENCES games(id)
);

-- 5. MATCHES tablosu
CREATE TABLE IF NOT EXISTS matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team1_id INT NOT NULL,
    team2_id INT NOT NULL,
    winner_id INT DEFAULT 0,
    team1_score INT DEFAULT 0,
    team2_score INT DEFAULT 0,
    match_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'SCHEDULED',
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (team1_id) REFERENCES teams(id),
    FOREIGN KEY (team2_id) REFERENCES teams(id)
);

-- 6. TEAM_MEMBERS tablosu (junction)
CREATE TABLE IF NOT EXISTS team_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    team_id INT NOT NULL,
    user_id INT NOT NULL,
    join_date DATE NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 7. TOURNAMENT_TEAMS tablosu (junction)
CREATE TABLE IF NOT EXISTS tournament_teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team_id INT NOT NULL,
    registration_date DATE NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

-- Örnek veriler
INSERT INTO users (username, password, email, role, status) VALUES
('admin', 'admin123', 'admin@esports.com', 'ADMIN', 'ACTIVE'),
('player1', 'pass123', 'player1@esports.com', 'MEMBER', 'ACTIVE'),
('player2', 'pass123', 'player2@esports.com', 'MEMBER', 'ACTIVE'),
('player3', 'pass123', 'player3@esports.com', 'MEMBER', 'ACTIVE'),
('player4', 'pass123', 'player4@esports.com', 'MEMBER', 'ACTIVE');

INSERT INTO games (name, genre, mode) VALUES
('Valorant', 'FPS', '5v5'),
('League of Legends', 'MOBA', '5v5'),
('CS2', 'FPS', '5v5');

INSERT INTO teams (name, game_id, max_capacity, status) VALUES
('Team Phoenix', 1, 5, 'ACTIVE'),
('Team Nova', 1, 5, 'ACTIVE'),
('Team Dragon', 2, 5, 'ACTIVE');

INSERT INTO tournaments (name, game_id, max_teams, start_date, end_date, status) VALUES
('Spring Cup 2026', 1, 8, '2026-04-01', '2026-04-30', 'UPCOMING');