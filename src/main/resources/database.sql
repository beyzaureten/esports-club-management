-- Additional sample data
INSERT INTO games (name, genre, mode) VALUES
('Dota 2', 'MOBA', '5v5'),
('Fortnite', 'Battle Royale', 'Solo'),
('Apex Legends', 'Battle Royale', '3v3'),
('Rocket League', 'Sports', '3v3'),
('Rainbow Six Siege', 'FPS', '5v5'),
('PUBG', 'Battle Royale', 'Solo'),
('Overwatch 2', 'FPS', '5v5');

INSERT INTO users (username, password, email, role, status) VALUES
('player5', 'pass123', 'player5@esports.com', 'MEMBER', 'ACTIVE'),
('player6', 'pass123', 'player6@esports.com', 'MEMBER', 'ACTIVE'),
('player7', 'pass123', 'player7@esports.com', 'MEMBER', 'ACTIVE'),
('player8', 'pass123', 'player8@esports.com', 'MEMBER', 'ACTIVE'),
('player9', 'pass123', 'player9@esports.com', 'MEMBER', 'ACTIVE'),
('player10', 'pass123', 'player10@esports.com', 'MEMBER', 'ACTIVE');

INSERT INTO teams (name, game_id, max_capacity, status) VALUES
('Team Alpha', 1, 5, 'ACTIVE'),
('Team Beta', 1, 5, 'ACTIVE'),
('Team Omega', 2, 5, 'ACTIVE'),
('Team Shadow', 3, 5, 'ACTIVE'),
('Team Blaze', 8, 5, 'ACTIVE'),
('Team Storm', 9, 5, 'ACTIVE');

INSERT INTO team_members (team_id, user_id, join_date) VALUES
(1, 2, '2026-03-01'),
(1, 3, '2026-03-01'),
(1, 4, '2026-03-02'),
(2, 5, '2026-03-03'),
(2, 6, '2026-03-03'),
(3, 7, '2026-03-04'),
(3, 8, '2026-03-04'),
(4, 9, '2026-03-05'),
(4, 10, '2026-03-05');

INSERT INTO tournaments (name, game_id, max_teams, start_date, end_date, status) VALUES
('Winter Championship 2026', 1, 8, '2026-03-01', '2026-03-31', 'FINISHED'),
('Summer Cup 2026', 2, 4, '2026-05-01', '2026-05-31', 'UPCOMING'),
('Pro League Season 1', 3, 8, '2026-04-15', '2026-05-15', 'UPCOMING');

INSERT INTO tournament_teams (tournament_id, team_id, registration_date) VALUES
(1, 1, '2026-02-25'),
(1, 2, '2026-02-26'),
(1, 3, '2026-02-27'),
(2, 3, '2026-04-01'),
(2, 4, '2026-04-02');

INSERT INTO matches (tournament_id, team1_id, team2_id, winner_id, team1_score, team2_score, match_date, status) VALUES
(1, 1, 2, 1, 13, 7, '2026-03-05', 'FINISHED'),
(1, 1, 3, 3, 10, 13, '2026-03-10', 'FINISHED'),
(1, 2, 3, 2, 13, 11, '2026-03-15', 'FINISHED'),
(2, 3, 4, 0, 0, 0, '2026-05-05', 'SCHEDULED');