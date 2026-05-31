package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Game;
import com.esportsclub.esports_management.repository.GameRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    public void deleteGame(int id) {
        gameRepository.deleteById(id);
    }
}
