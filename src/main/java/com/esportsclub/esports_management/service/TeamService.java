package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.repository.TeamRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public void saveTeam(Team team) {
        teamRepository.save(team);
    }

    public void deleteTeam(int id) {
        teamRepository.deleteById(id);
    }

    public List<Team> getActiveTeams() {
        return teamRepository.findByStatus("ACTIVE");
    }
}
