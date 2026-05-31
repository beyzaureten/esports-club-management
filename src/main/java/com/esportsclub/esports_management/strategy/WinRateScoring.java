package com.esportsclub.esports_management.strategy;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.service.MatchService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("winrate")
public class WinRateScoring implements ScoringStrategy {

    private final MatchService matchService;

    public WinRateScoring(@Lazy MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public List<Team> sort(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return teams;
        }

        List<Match> allMatches = matchService.getAllMatches();

        Map<Integer, Integer> winCounts = new HashMap<>();
        Map<Integer, Integer> matchCounts = new HashMap<>();

        for (Team team : teams) {
            winCounts.put(team.getId(), 0);
            matchCounts.put(team.getId(), 0);
        }

        if (allMatches != null) {
            for (Match match : allMatches) {

                if (match == null) {
                    continue;
                }

                String status = match.getStatus();

                if (status == null || !"FINISHED".equalsIgnoreCase(status)) {
                    continue;
                }

                Integer team1Id = match.getTeam1Id();
                Integer team2Id = match.getTeam2Id();
                Integer winnerId = match.getWinnerId();

                if (team1Id != null) {
                    matchCounts.put(team1Id, matchCounts.getOrDefault(team1Id, 0) + 1);
                }

                if (team2Id != null) {
                    matchCounts.put(team2Id, matchCounts.getOrDefault(team2Id, 0) + 1);
                }

                if (winnerId != null && winnerId > 0) {
                    winCounts.put(winnerId, winCounts.getOrDefault(winnerId, 0) + 1);
                }
            }
        }

        return teams.stream()
                .sorted((t1, t2) -> {
                    int team1Matches = matchCounts.getOrDefault(t1.getId(), 0);
                    int team2Matches = matchCounts.getOrDefault(t2.getId(), 0);

                    int team1Wins = winCounts.getOrDefault(t1.getId(), 0);
                    int team2Wins = winCounts.getOrDefault(t2.getId(), 0);

                    double team1Rate = team1Matches > 0 ? (double) team1Wins / team1Matches : 0.0;
                    double team2Rate = team2Matches > 0 ? (double) team2Wins / team2Matches : 0.0;

                    return Double.compare(team2Rate, team1Rate);
                })
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Win Rate Scoring";
    }
}