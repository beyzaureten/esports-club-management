package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByName(String name);
}
