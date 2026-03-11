package com.esportsclub.factory;

import com.esportsclub.model.Team;

public class TeamFactory {

    // Factory Pattern - creates different types of teams
    public static Team createTeam(String type, String name, int gameId) {
        switch (type.toUpperCase()) {
            case "COMPETITIVE":
                return new Team(0, name, gameId, 5, "ACTIVE");
            case "CASUAL":
                return new Team(0, name, gameId, 10, "ACTIVE");
            case "TRAINING":
                return new Team(0, name, gameId, 3, "ACTIVE");
            default:
                System.out.println("Unknown team type, creating default team.");
                return new Team(0, name, gameId, 5, "ACTIVE");
        }
    }
}