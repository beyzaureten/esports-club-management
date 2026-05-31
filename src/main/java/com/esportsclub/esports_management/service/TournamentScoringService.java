package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.strategy.ScoringStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentScoringService {

    private ScoringStrategy strategy;

    public TournamentScoringService(@Qualifier("points") ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Team> getSortedTeams(List<Team> teams) {
        return strategy.sort(teams);
    }

    public void setStrategy(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    public String getCurrentStrategyName() {
        return strategy.getStrategyName();
    }
}