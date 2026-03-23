package com.esportsclub.service;

import com.esportsclub.dao.GameDAO;
import com.esportsclub.dao.TeamDAO;
import com.esportsclub.model.Game;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameService {

    private final GameDAO gameDAO;
    private final TeamDAO teamDAO;

    public GameService() {
        this.gameDAO = new GameDAO();
        this.teamDAO = new TeamDAO();
    }

    // Add new game
    public boolean addGame(String name, String genre, String mode) {
        String err = validate(name, genre, mode);
        if (err != null) { log("addGame", err); return false; }

        if (isDuplicateName(name, -1)) {
            log("addGame", "A game named '" + name.trim() + "' already exists.");
            return false;
        }
        gameDAO.insert(new Game(0, name.trim(), genre.trim(), mode.trim()));
        return true;
    }

    // Update game
    public boolean updateGame(Game game) {
        if (game == null) { log("updateGame", "game object is null"); return false; }
        String err = validate(game.getName(), game.getGenre(), game.getMode());
        if (err != null) { log("updateGame", err); return false; }

        if (isDuplicateName(game.getName(), game.getId())) {
            log("updateGame", "Another game with name '" + game.getName().trim() + "' already exists.");
            return false;
        }
        gameDAO.update(game);
        return true;
    }

    // Delete game
    public String deleteGame(int id) {
        int teamCount = teamDAO.getByGameId(id).size();
        if (teamCount > 0) {
            return "Cannot delete: " + teamCount + " team(s) are linked to this game. "
                    + "Remove or reassign the teams first.";
        }
        gameDAO.delete(id);
        return null;
    }

    // Get game by id
    public Optional<Game> getGameById(int id) {
        return Optional.ofNullable(gameDAO.getById(id));
    }

    // Get all games
    public List<Game> getAllGames() {
        return gameDAO.getAll();
    }

    // Search games by keyword
    public List<Game> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAllGames();
        String kw = keyword.trim().toLowerCase();
        return gameDAO.getAll().stream()
                .filter(g -> g.getName().toLowerCase().contains(kw)
                        || g.getGenre().toLowerCase().contains(kw)
                        || g.getMode().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // Get games by genre
    public List<Game> getByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty() || "All".equalsIgnoreCase(genre))
            return getAllGames();
        return gameDAO.getAll().stream()
                .filter(g -> g.getGenre().equalsIgnoreCase(genre.trim()))
                .collect(Collectors.toList());
    }

    // Get all genres
    public List<String> getAllGenres() {
        return gameDAO.getAll().stream()
                .map(Game::getGenre)
                .map(String::trim)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    // Get team count for a game
    public int getTeamCountForGame(int gameId) {
        return teamDAO.getByGameId(gameId).size();
    }

    // Validation
    private String validate(String name, String genre, String mode) {
        if (name  == null || name.trim().isEmpty())  return "Game name cannot be empty.";
        if (genre == null || genre.trim().isEmpty()) return "Genre cannot be empty.";
        if (mode  == null || mode.trim().isEmpty())  return "Mode cannot be empty.";
        if (name.trim().length()  > 100) return "Game name cannot exceed 100 characters.";
        if (genre.trim().length() > 50)  return "Genre cannot exceed 50 characters.";
        if (mode.trim().length()  > 20)  return "Mode cannot exceed 20 characters.";
        return null;
    }

    private boolean isDuplicateName(String name, int excludeId) {
        return gameDAO.getAll().stream()
                .anyMatch(g -> g.getName().trim().equalsIgnoreCase(name.trim())
                        && g.getId() != excludeId);
    }

    private void log(String method, String msg) {
        System.out.println("[GameService." + method + "] " + msg);
    }
}