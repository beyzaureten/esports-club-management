package com.esportsclub.esports_management.strategy;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.service.MatchService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("points")
public class PointsBasedScoring implements ScoringStrategy {

    private final MatchService matchService;

    public PointsBasedScoring(@Lazy MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public List<Team> sort(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return teams;
        }

        List<Match> allMatches = matchService.getAllMatches();
        Map<Integer, Integer> teamScores = new HashMap<>();

        for (Team team : teams) {
            teamScores.put(team.getId(), 0);
        }

        if (allMatches != null) {
            for (Match match : allMatches) {

                if (match == null) {
                    continue;
                }

                if (match.getStatus() == null || !"FINISHED".equalsIgnoreCase(match.getStatus())) {
                    continue;
                }

                Integer winnerId = match.getWinnerId();
                Integer team1Id = match.getTeam1Id();
                Integer team2Id = match.getTeam2Id();

                int team1Score = match.getTeam1Score() != null ? match.getTeam1Score() : 0;
                int team2Score = match.getTeam2Score() != null ? match.getTeam2Score() : 0;

                if (winnerId != null && winnerId > 0) {
                    teamScores.put(winnerId, teamScores.getOrDefault(winnerId, 0) + 3);
                }

                if (team1Id != null) {
                    teamScores.put(team1Id, teamScores.getOrDefault(team1Id, 0) + team1Score);
                }

                if (team2Id != null) {
                    teamScores.put(team2Id, teamScores.getOrDefault(team2Id, 0) + team2Score);
                }
            }
        }

        return teams.stream()
                .sorted((t1, t2) ->
                        Integer.compare(
                                teamScores.getOrDefault(t2.getId(), 0),
                                teamScores.getOrDefault(t1.getId(), 0)
                        )
                )
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Points Based Scoring";
    }
}