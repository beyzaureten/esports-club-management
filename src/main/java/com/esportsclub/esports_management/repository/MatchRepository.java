package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentId(int tournamentId);
    List<Match> findByStatus(String status);
    List<Match> findByTeam1IdOrTeam2Id(int team1Id, int team2Id);
}
