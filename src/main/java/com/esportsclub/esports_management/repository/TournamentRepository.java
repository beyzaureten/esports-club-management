package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    List<Tournament> findByStatus(String status);
    List<Tournament> findByGameId(int gameId);
}
