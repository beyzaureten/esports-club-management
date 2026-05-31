package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByStatus(String status);
    List<Team> findByGameId(int gameId);
}
