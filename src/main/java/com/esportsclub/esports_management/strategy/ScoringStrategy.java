package com.esportsclub.esports_management.strategy;

import com.esportsclub.esports_management.model.Team;
import java.util.List;

public interface ScoringStrategy {
    List<Team> sort(List<Team> teams);
    String getStrategyName();
}